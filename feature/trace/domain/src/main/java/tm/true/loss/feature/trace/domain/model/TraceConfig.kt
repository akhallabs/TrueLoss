package tm.true.loss.feature.trace.domain.model
enum class TraceProtocol { ICMP, UDP, TCP }
enum class IpVersion { IPv4, IPv6 }
data class TraceConfig(
    val target: String,
    val protocol: TraceProtocol = TraceProtocol.ICMP,
    val ipVersion: IpVersion = IpVersion.IPv4,
    val maxHops: Int = 30,
    val timeoutMs: Int = 3000
)
