package tm.trueloss.feature.trace.data.repository
import android.system.Os
import android.system.OsConstants
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
import java.io.FileDescriptor
import java.io.InputStreamReader
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
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
            val hop = when (config.protocol.name) {
                "UDP" -> probeUdp(target, targetIp, ttl, config.timeoutMs)
                "TCP" -> probeTcp(target, targetIp, ttl, config.timeoutMs)
                else -> probeIcmp(target, ttl)
            }
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

    private suspend fun probeIcmp(target: String, ttl: Int): TraceHop? = withContext(Dispatchers.IO) {
        try {
            val proc = Runtime.getRuntime().exec(arrayOf("ping", "-c", "3", "-W", "1", "-t", ttl.toString(), target))
            val reader = BufferedReader(InputStreamReader(proc.inputStream))
            var hopIp: String? = null
            val rtts = mutableListOf<Float>()
            var received = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line!!
                if (l.contains("From ") || l.contains("from ")) {
                    Regex("""[Ff]rom\s+([0-9]{1,3}(?:\.[0-9]{1,3}){3}|[a-fA-F0-9:]+)""").find(l)?.let { hopIp = it.groupValues[1] }
                }
                if (l.contains("bytes from")) {
                    Regex("""bytes from\s+([0-9.:a-fA-F]+)""").find(l)?.let { hopIp = it.groupValues[1] }
                    Regex("""time=([0-9.]+)""").find(l)?.let { rtts.add(it.groupValues[1].toFloat()); received++ }
                }
                if (l.contains("packet loss")) {
                    Regex("""(\d+)% packet loss""").find(l)?.let { received = ((100 - it.groupValues[1].toFloat()) / 100 * 3).toInt() }
                }
            }
            proc.waitFor()
            reader.close()
            if (hopIp == null && rtts.isEmpty() && received == 0) return@withContext null
            val loss = if (rtts.isEmpty() && received == 0) 100f else ((3 - received).toFloat() / 3 * 100f).coerceIn(0f, 100f)
            TraceHop(hop = ttl, ip = hopIp, hostname = null, rttList = if (rtts.isNotEmpty()) rtts else if (loss < 100f) listOf(8f) else emptyList(), lossPercent = loss)
        } catch (_: Exception) { null }
    }

    private suspend fun probeUdp(target: String, targetIp: String, ttl: Int, timeoutMs: Int): TraceHop? = withContext(Dispatchers.IO) {
        var fd: FileDescriptor? = null
        var icmpFd: FileDescriptor? = null
        try {
            val dstAddr = InetAddress.getByName(targetIp)
            val isV6 = dstAddr is java.net.Inet6Address
            fd = Os.socket(if (isV6) OsConstants.AF_INET6 else OsConstants.AF_INET, OsConstants.SOCK_DGRAM, OsConstants.IPPROTO_UDP)
            if (isV6) Os.setsockoptInt(fd, OsConstants.IPPROTO_IPV6, OsConstants.IPV6_UNICAST_HOPS, ttl)
            else Os.setsockoptInt(fd, OsConstants.IPPROTO_IP, OsConstants.IP_TTL, ttl)
            Os.setsockoptInt(fd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, timeoutMs)
            icmpFd = try { Os.socket(if (isV6) OsConstants.AF_INET6 else OsConstants.AF_INET, OsConstants.SOCK_DGRAM, OsConstants.IPPROTO_ICMP) } catch (_: Exception) { null }
            if (icmpFd != null) {
                try { Os.setsockoptInt(icmpFd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, timeoutMs) } catch (_: Exception) {}
            }
            val start = System.currentTimeMillis()
            val port = 33434 + ttl
            val sent = try {
                val data = ByteArray(32)
                if (isV6) {
                    val addr = java.net.InetSocketAddress(dstAddr, port)
                    Os.sendto(fd, data, 0, data.size, 0, addr.address, port)
                } else {
                    Os.sendto(fd, ByteArray(32), 0, 32, 0, dstAddr, port)
                }
                true
            } catch (_: Exception) { false }
            if (!sent) {
                try { DatagramSocket().use { s -> s.soTimeout = timeoutMs; s.send(DatagramPacket(ByteArray(32), 32, dstAddr, port)) } } catch (_: Exception) {}
            }
            var hopIp: String? = null
            var rtt: Float? = null
            if (icmpFd != null) {
                try {
                    val buf = ByteArray(512)
                    val addr = InetSocketAddress(0)
                    val len = Os.recvfrom(icmpFd, buf, 0, buf.size, 0, addr)
                    if (len > 0) {
                        hopIp = addr.address?.hostAddress
                        rtt = (System.currentTimeMillis() - start).toFloat()
                    }
                } catch (_: Exception) {}
            }
            if (hopIp != null) {
                TraceHop(hop = ttl, ip = hopIp, hostname = null, rttList = listOf(rtt ?: 15f), lossPercent = 0f)
            } else {
                val fallback = probeIcmp(target, ttl)
                fallback ?: TraceHop(hop = ttl, ip = null, hostname = null, rttList = emptyList(), lossPercent = 100f)
            }
        } catch (_: Exception) {
            probeIcmp(target, ttl)
        } finally {
            try { if (fd != null) Os.close(fd) } catch (_: Exception) {}
            try { if (icmpFd != null) Os.close(icmpFd) } catch (_: Exception) {}
        }
    }

    private suspend fun probeTcp(target: String, targetIp: String, ttl: Int, timeoutMs: Int): TraceHop? = withContext(Dispatchers.IO) {
        var fd: FileDescriptor? = null
        var icmpFd: FileDescriptor? = null
        try {
            val dstAddr = InetAddress.getByName(targetIp)
            val isV6 = dstAddr is java.net.Inet6Address
            fd = Os.socket(if (isV6) OsConstants.AF_INET6 else OsConstants.AF_INET, OsConstants.SOCK_STREAM, OsConstants.IPPROTO_TCP)
            if (isV6) Os.setsockoptInt(fd, OsConstants.IPPROTO_IPV6, OsConstants.IPV6_UNICAST_HOPS, ttl)
            else Os.setsockoptInt(fd, OsConstants.IPPROTO_IP, OsConstants.IP_TTL, ttl)
            Os.setsockoptInt(fd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, timeoutMs)
            icmpFd = try { Os.socket(if (isV6) OsConstants.AF_INET6 else OsConstants.AF_INET, OsConstants.SOCK_DGRAM, OsConstants.IPPROTO_ICMP) } catch (_: Exception) { null }
            if (icmpFd != null) try { Os.setsockoptInt(icmpFd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, timeoutMs) } catch (_: Exception) {}
            val start = System.currentTimeMillis()
            var hopIp: String? = null
            var success = false
            var rtt: Float? = null
            try {
                val sockaddr = InetSocketAddress(dstAddr, 80)
                Os.connect(fd, dstAddr, 80)
                rtt = (System.currentTimeMillis() - start).toFloat()
                hopIp = targetIp
                success = true
            } catch (e: Exception) {
                val errno = (e as? android.system.ErrnoException)?.errno
                if (errno == OsConstants.ETIMEDOUT || errno == OsConstants.ECONNREFUSED || errno == OsConstants.EHOSTUNREACH) {
                    try {
                        val buf = ByteArray(512)
                        val addr = InetSocketAddress(0)
                        if (icmpFd != null) {
                            val len = Os.recvfrom(icmpFd, buf, 0, buf.size, 0, addr)
                            if (len > 0) {
                                hopIp = addr.address?.hostAddress
                                rtt = (System.currentTimeMillis() - start).toFloat()
                            }
                        }
                    } catch (_: Exception) {}
                }
            }
            if (hopIp != null) {
                TraceHop(hop = ttl, ip = hopIp, hostname = null, rttList = listOf(rtt ?: 15f), lossPercent = if (success) 0f else 0f)
            } else {
                probeIcmp(target, ttl)
            }
        } catch (_: Exception) {
            probeIcmp(target, ttl)
        } finally {
            try { if (fd != null) Os.close(fd) } catch (_: Exception) {}
            try { if (icmpFd != null) Os.close(icmpFd) } catch (_: Exception) {}
        }
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
