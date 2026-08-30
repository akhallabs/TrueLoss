package tm.true.loss.feature.trace.ui
import tm.true.loss.feature.trace.domain.model.TraceConfig
import tm.true.loss.feature.trace.domain.model.TraceHop
import tm.true.loss.feature.trace.domain.model.TraceProtocol
import tm.true.loss.feature.trace.domain.model.IpVersion
data class TraceUiState(
    val target: String = "",
    val protocol: TraceProtocol = TraceProtocol.ICMP,
    val ipVersion: IpVersion = IpVersion.IPv4,
    val hops: List<TraceHop> = emptyList(),
    val isRunning: Boolean = false,
    val error: String? = null,
    val avgLoss: Float = 0f,
    val maxLoss: Float = 0f,
    val completedHops: Int = 0
) {
    val isTargetValid: Boolean get() = target.isNotBlank() && target.length >= 3
    val config: TraceConfig get() = TraceConfig(target = target.trim(), protocol = protocol, ipVersion = ipVersion)
}
sealed interface TraceEvent {
    data class TargetChanged(val v: String): TraceEvent
    data class ProtocolChanged(val p: TraceProtocol): TraceEvent
    data class IpVersionChanged(val v: IpVersion): TraceEvent
    data object Start: TraceEvent
    data object Stop: TraceEvent
    data object Clear: TraceEvent
}
sealed interface TraceEffect { data class Share(val id: String): TraceEffect }
