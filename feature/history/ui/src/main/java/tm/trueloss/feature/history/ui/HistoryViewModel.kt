package tm.trueloss.feature.history.ui
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tm.trueloss.core.ui.base.BaseViewModel
import tm.trueloss.feature.history.domain.repository.HistoryRepository
import tm.trueloss.feature.history.domain.usecase.GetHistoryUseCase
import javax.inject.Inject
@HiltViewModel
class HistoryViewModel @Inject constructor(private val getHistory: GetHistoryUseCase, private val repo: HistoryRepository) : BaseViewModel<HistoryUiState, HistoryEvent, HistoryEffect>(HistoryUiState(isLoading = true)) {
    init { viewModelScope.launch { getHistory().collectLatest { setState { copy(items = it, isLoading = false) } } } }
    override fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.Delete -> viewModelScope.launch { repo.delete(event.id) }
            is HistoryEvent.Clear -> viewModelScope.launch { repo.clear() }
            is HistoryEvent.Open -> {}
        }
    }
}
