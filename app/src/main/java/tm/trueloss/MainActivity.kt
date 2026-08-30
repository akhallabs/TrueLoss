package tm.trueloss
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import tm.trueloss.core.datastore.PreferencesManager
import tm.trueloss.core.designsystem.theme.TrueLossTheme
import tm.trueloss.navigation.TrueLossNavHost
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var prefs: PreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by prefs.themeFlow.collectAsState(initial = "system")
            val dark = when (theme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            TrueLossTheme(darkTheme = dark) { TrueLossNavHost() }
        }
    }
}
