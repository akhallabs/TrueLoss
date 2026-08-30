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
import java.net.InetAddress
import javax.inject.Inject
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
class TraceRepositoryImpl @Inject constructor() : TraceRepository {
    override fun trace(config: TraceConfig): Flow<List<TraceHop>> = flow {
        val target = config.target.trim()
        val ip = try { InetAddress.getByName(target).hostAddress ?: target } catch (_: Exception) { target }
        val results = mutableListOf<TraceHop>()
        val maxHops = config.maxHops.coerceIn(1, 30)
        for (ttl in 1..maxHops) {
            val hop = probeWithTtl(target, ttl, config)
            if (hop != null) {
                val enriched = enrichHop(hop, config)
                results.add(enriched)
                emit(results.toList())
                if (hop.ip == ip || hop.ip == target) break
                if (results.size >= 1 && ttl > 5 && results.takeLast(2).all { it.lossPercent == 100f }) {
                    if (ttl > 12) break
                }
            } else {
                val timeoutHop = TraceHop(hop = ttl, ip = null, hostname = null, rttList = emptyList(), lossPercent = 100f)
                results.add(timeoutHop)
                emit(results.toList())
            }
            delay(300)
            if (results.size >= maxHops) break
        }
        if (results.isEmpty()) {
            val direct = pingLoss(target, 4)
            val hop = TraceHop(hop = 1, ip = ip, hostname = target, rttList = direct.second, lossPercent = direct.first)
            emit(listOf(enrichHop(hop, config)))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun probeWithTtl(target: String, ttl: Int, config: TraceConfig): TraceHop? = withContext(Dispatchers.IO) {
        try {
            val pingCount = 3
            val cmd = when (config.protocol.name) {
                "TCP" -> arrayOf("ping", "-c", pingCount.toString(), "-W", "1", "-t", ttl.toString(), target)
                "UDP" -> arrayOf("ping", "-c", pingCount.toString(), "-W", "1", "-t", ttl.toString(), target)
                else -> arrayOf("ping", "-c", pingCount.toString(), "-W", "1", "-t", ttl.toString(), target)
            }
            val proc = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(proc.inputStream))
            var hopIp: String? = null
            val rtts = mutableListOf<Float>()
            var transmitted = pingCount
            var received = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!
                if (l.contains("From ")) {
                    val m = Regex("""From\s+([0-9.]+)""").find(l)
                    if (m != null) hopIp = m.groupValues[1]
                    val m2 = Regex("""from\s+([0-9.]+)""").find(l)
                    if (m2 != null && hopIp == null) hopIp = m2.groupValues[1]
                }
                if (l.contains("bytes from")) {
                    val m = Regex("""from\s+([0-9.]+)""").find(l)
                    if (m != null) hopIp = m.groupValues[1]
                    val t = Regex("""time=([0-9.]+)""").find(l)
                    if (t != null) { rtts.add(t.groupValues[1].toFloat()); received++ }
                }
                if (l.contains("packet loss")) {
                    val m = Regex("""(\d+)% packet loss""").find(l)
                    if (m != null) {
                        val loss = m.groupValues[1].toFloat()
                        transmitted = pingCount
                        received = ((100 - loss) / 100 * pingCount).toInt()
                    }
                }
                if (l.contains("rtt min")) {
                    val m = Regex("""rtt min/avg/max[^=]*=\s*([0-9.]+)/([0-9.]+)/([0-9.]+)""").find(l)
                    if (m != null && rtts.isEmpty()) { rtts.add(m.groupValues[2].toFloat()) }
                }
            }
            proc.waitFor()
            reader.close()
            if (hopIp == null && rtts.isEmpty() && received == 0) return@withContext null
            val loss = if (transmitted > 0) ((transmitted - received).toFloat() / transmitted * 100f).coerceIn(0f, 100f) else if (rtts.isEmpty()) 100f else 0f
            TraceHop(hop = ttl, ip = hopIp, hostname = null, rttList = rtts.ifEmpty { if (loss == 100f) emptyList() else listOf(10f) }, lossPercent = loss)
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
                val t = Regex("""time=([0-9.]+)""").find(l)
                if (t != null) rtts.add(t.groupValues[1].toFloat())
                val m = Regex("""(\d+)% packet loss""").find(l)
                if (m != null) loss = m.groupValues[1].toFloat()
            }
            proc.waitFor()
            reader.close()
            if (rtts.isEmpty() && loss == 100f) {
                val start = System.currentTimeMillis()
                val reachable = try { InetAddress.getByName(target).isReachable(2000) } catch (_: Exception) { false }
                val rtt = (System.currentTimeMillis() - start).toFloat().coerceAtLeast(1f)
                if (reachable) return@withContext 0f to listOf(rtt)
            }
            loss to rtts
        } catch (_: Exception) { 100f to emptyList() }
    }

    private suspend fun enrichHop(hop: TraceHop, config: TraceConfig): TraceHop {
        val ip = hop.ip ?: return hop
        val geo = resolveGeo(ip)
        val asn = resolveAsn(ip)
        return hop.copy(asn = asn, country = geo.first, city = geo.second)
    }

    override suspend fun resolveAsn(ip: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://ip-api.com/json/$ip?fields=as")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) {
                val txt = conn.inputStream.bufferedReader().readText()
                val obj = JSONObject(txt)
                obj.optString("as", null)?.takeIf { it.isNotBlank() }
            } else null
        } catch (_: Exception) { null }
    }

    override suspend fun resolveGeo(ip: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://ip-api.com/json/$ip?fields=country,regionName,city")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) {
                val txt = conn.inputStream.bufferedReader().readText()
                val obj = JSONObject(txt)
                val c = obj.optString("country", null)?.takeIf { it.isNotBlank() }
                val city = obj.optString("city", null)?.takeIf { it.isNotBlank() } ?: obj.optString("regionName", null)
                c to city
            } else null to null
        } catch (_: Exception) { null to null }
    }
}
