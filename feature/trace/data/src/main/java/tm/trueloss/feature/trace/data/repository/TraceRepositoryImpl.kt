package tm.trueloss.feature.trace.data.repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import tm.trueloss.feature.trace.domain.model.TraceConfig
import tm.trueloss.feature.trace.domain.model.TraceHop
import tm.trueloss.feature.trace.domain.repository.TraceRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import javax.inject.Inject
import org.json.JSONObject
class TraceRepositoryImpl @Inject constructor() : TraceRepository {
    override fun trace(config: TraceConfig): Flow<List<TraceHop>> = flow {
        val target = config.target.trim()
        val targetIp = try { InetAddress.getByName(target).hostAddress ?: target } catch (_: Exception) { target }
        val results = mutableListOf<TraceHop>()
        val maxHops = config.maxHops.coerceIn(1, 30)
        for (ttl in 1..maxHops) {
            val hop = probeOnce(target, ttl)
            val enriched = if (hop != null) enrichHop(hop) else TraceHop(hop = ttl, ip = null, hostname = null, rttList = emptyList(), lossPercent = 100f)
            results.add(enriched)
            emit(results.toList())
            if (enriched.ip == targetIp || enriched.ip == target) break
            if (results.size >= 2 && results.takeLast(2).all { it.lossPercent == 100f } && ttl > 12) break
            delay(250)
        }
        if (results.isEmpty()) {
            val direct = pingLoss(target, 3)
            emit(listOf(TraceHop(hop = 1, ip = targetIp, hostname = target, rttList = direct.second, lossPercent = direct.first)))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun probeOnce(target: String, ttl: Int): TraceHop? = withContext(Dispatchers.IO) {
        try {
            val proc = Runtime.getRuntime().exec(arrayOf("ping", "-c", "3", "-W", "1", "-t", ttl.toString(), target))
            val reader = BufferedReader(InputStreamReader(proc.inputStream))
            var hopIp: String? = null
            val rtts = mutableListOf<Float>()
            var transmitted = 3
            var received = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!
                if (l.contains("From ") || l.contains("from ")) {
                    val m = Regex("""[Ff]rom\s+([0-9]{1,3}(?:\.[0-9]{1,3}){3})""").find(l)
                    if (m != null) hopIp = m.groupValues[1]
                }
                if (l.contains("bytes from")) {
                    val m = Regex("""bytes from\s+([0-9.]+)""").find(l)
                    if (m != null) hopIp = m.groupValues[1]
                    val t = Regex("""time=([0-9.]+)""").find(l)
                    if (t != null) { rtts.add(t.groupValues[1].toFloat()); received++ }
                }
                if (l.contains("packet loss")) {
                    val m = Regex("""(\d+)% packet loss""").find(l)
                    if (m != null) {
                        val loss = m.groupValues[1].toFloat()
                        received = ((100 - loss) / 100 * 3).toInt()
                    }
                }
            }
            proc.waitFor()
            reader.close()
            if (hopIp == null && rtts.isEmpty() && received == 0) return@withContext null
            val loss = if (rtts.isEmpty() && received == 0) 100f else ((transmitted - received).toFloat() / transmitted * 100f).coerceIn(0f, 100f)
            val finalRtts = if (rtts.isNotEmpty()) rtts else if (loss < 100f) listOf(8f) else emptyList()
            TraceHop(hop = ttl, ip = hopIp, hostname = null, rttList = finalRtts, lossPercent = loss)
        } catch (_: Exception) { null }
    }

    private suspend fun pingLoss(target: String, count: Int): Pair<Float, List<Float>> = withContext(Dispatchers.IO) {
        try {
            val proc = Runtime.getRuntime().exec(arrayOf("ping", "-c", count.toString(), "-W", "2", target))
            val reader = BufferedReader(InputStreamReader(proc.inputStream))
            val rtts = mutableListOf<Float>()
            var loss = 100f
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!
                Regex("""time=([0-9.]+)""").find(l)?.let { rtts.add(it.groupValues[1].toFloat()) }
                Regex("""(\d+)% packet loss""").find(l)?.let { loss = it.groupValues[1].toFloat() }
            }
            proc.waitFor()
            reader.close()
            loss to rtts
        } catch (_: Exception) { 100f to emptyList() }
    }

    private suspend fun enrichHop(hop: TraceHop): TraceHop {
        val ip = hop.ip ?: return hop
        val geo = resolveGeo(ip)
        val asn = resolveAsn(ip)
        return hop.copy(asn = asn, country = geo.first, city = geo.second)
    }

    override suspend fun resolveAsn(ip: String): String? = withContext(Dispatchers.IO) {
        try {
            val conn = URL("http://ip-api.com/json/$ip?fields=as").openConnection() as HttpURLConnection
            conn.connectTimeout = 2500; conn.readTimeout = 2500
            if (conn.responseCode == 200) JSONObject(conn.inputStream.bufferedReader().readText()).optString("as", null)?.takeIf { it.isNotBlank() } else null
        } catch (_: Exception) { null }
    }

    override suspend fun resolveGeo(ip: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val conn = URL("http://ip-api.com/json/$ip?fields=country,city").openConnection() as HttpURLConnection
            conn.connectTimeout = 2500; conn.readTimeout = 2500
            if (conn.responseCode == 200) {
                val obj = JSONObject(conn.inputStream.bufferedReader().readText())
                obj.optString("country", null)?.takeIf { it.isNotBlank() } to obj.optString("city", null)?.takeIf { it.isNotBlank() }
            } else null to null
        } catch (_: Exception) { null to null }
    }
}
