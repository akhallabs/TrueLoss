package tm.trueloss.feature.settings.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tm.trueloss.feature.settings.ui.SettingsScreen
fun NavGraphBuilder.settingsGraph(){ composable("settings"){ SettingsScreen() } }
