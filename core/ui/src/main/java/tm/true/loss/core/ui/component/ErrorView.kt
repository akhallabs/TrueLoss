package tm.true.loss.core.ui.component
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
@Composable fun ErrorView(message: String, onRetry: (() -> Unit)? = null) { Text(text = message) }
