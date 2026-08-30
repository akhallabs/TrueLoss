package tm.true.loss.navigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tm.true.loss.feature.history.ui.HistoryScreen
import tm.true.loss.feature.trace.ui.TraceScreen
import tm.true.loss.feature.settings.ui.SettingsScreen
import tm.true.loss.feature.result.ui.ResultScreen
@Composable
fun TrueLossNavHost() {
    val nav = rememberNavController()
    var selected by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selected == 0, onClick = { selected = 0; nav.navigate("trace") { launchSingleTop = true } }, icon = { Icon(Icons.Default.TravelExplore, null) }, label = { Text("Kontrol") })
                NavigationBarItem(selected = selected == 1, onClick = { selected = 1; nav.navigate("history") { launchSingleTop = true } }, icon = { Icon(Icons.Default.History, null) }, label = { Text("Geçmiş") })
                NavigationBarItem(selected = selected == 2, onClick = { selected = 2; nav.navigate("settings") { launchSingleTop = true } }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Ayarlar") })
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "trace", modifier = Modifier.padding(padding)) {
            composable("trace") { TraceScreen() }
            composable("history") { HistoryScreen() }
            composable("result/{traceId}") { ResultScreen(it.arguments?.getString("traceId") ?: "") }
            composable("settings") { SettingsScreen() }
        }
    }
}
