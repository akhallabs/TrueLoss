package tm.trueloss.feature.result.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tm.trueloss.feature.result.ui.ResultScreen
fun NavGraphBuilder.resultGraph(){ composable("result/{traceId}"){ backStackEntry -> ResultScreen(backStackEntry.arguments?.getString("traceId") ?: "") } }
