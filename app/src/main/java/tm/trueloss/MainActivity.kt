package tm.trueloss
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import tm.trueloss.core.designsystem.theme.TrueLossTheme
import tm.trueloss.navigation.TrueLossNavHost
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TrueLossTheme { TrueLossNavHost() } }
    }
}
