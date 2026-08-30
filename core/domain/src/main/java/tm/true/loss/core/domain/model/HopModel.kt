package tm.true.loss.core.domain.model

data class HopModel(
    val index: Int,
    val ip: String?,
    val hostname: String?,
    val rttMs: List<Float>,
    val lossPercent: Float,
    val asn: String?,
    val country: String?,
    val city: String?
)
data class TraceModel(
    val id: String,
    val target: String,
    val protocol: String, // ICMP / UDP / TCP
    val ipVersion: String, // IPv4 / IPv6
    val hops: List<HopModel>,
    val createdAt: Long
)
