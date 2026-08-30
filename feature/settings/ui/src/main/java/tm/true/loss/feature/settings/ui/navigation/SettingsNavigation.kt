package tm.true.loss.feature.settings.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import tm.true.loss.feature.settings.ui.SettingsScreen
fun NavGraphBuilder.settingsGraph(){ composable("settings"){ SettingsScreen() } }
