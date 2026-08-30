package tm.true.loss.feature.history.ui
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
@Composable fun HistoryScreen(vm: HistoryViewModel = hiltViewModel()){
    val state by vm.uiState.collectAsStateWithLifecycle()
    LazyColumn{ items(state.items){ Text("${it.target} - ${it.date}") } }
}
