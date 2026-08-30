package tm.trueloss.core.navigation
import kotlinx.serialization.Serializable
sealed interface Route {
    @Serializable data object Trace : Route
    @Serializable data object History : Route
    @Serializable data class Result(val traceId: String) : Route
    @Serializable data object Settings : Route
}
