package tm.true.loss.feature.trace.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tm.true.loss.feature.trace.ui.TraceScreen
fun NavGraphBuilder.traceGraph() { composable("trace") { TraceScreen() } }
