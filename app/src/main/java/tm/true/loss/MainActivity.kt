package tm.true.loss
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import tm.true.loss.core.designsystem.theme.TrueLossTheme
import tm.true.loss.navigation.TrueLossNavHost
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TrueLossTheme { TrueLossNavHost() } }
    }
}
