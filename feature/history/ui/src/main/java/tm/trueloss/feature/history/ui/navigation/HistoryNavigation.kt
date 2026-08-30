package tm.trueloss.feature.history.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tm.trueloss.feature.history.ui.HistoryScreen
fun NavGraphBuilder.historyGraph(){ composable("history"){ HistoryScreen() } }
