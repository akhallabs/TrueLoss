package tm.trueloss.feature.trace.domain.model
data class TraceHop(
    val hop: Int,
    val ip: String?,
    val hostname: String?,
    val rttList: List<Float>,
    val lossPercent: Float,
    val asn: String? = null,
    val country: String? = null,
    val city: String? = null
)
