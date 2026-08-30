package tm.trueloss.feature.trace.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tm.trueloss.feature.trace.ui.TraceScreen
fun NavGraphBuilder.traceGraph() { composable("trace") { TraceScreen() } }
