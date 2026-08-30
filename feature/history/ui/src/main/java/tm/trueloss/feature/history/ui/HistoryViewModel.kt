package tm.trueloss.feature.history.ui
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tm.trueloss.core.ui.base.BaseViewModel
import tm.trueloss.feature.history.domain.usecase.GetHistoryUseCase
import javax.inject.Inject
@HiltViewModel class HistoryViewModel @Inject constructor(private val getHistory: GetHistoryUseCase): BaseViewModel<HistoryUiState, HistoryEvent, HistoryEffect>(HistoryUiState()){
    init{ viewModelScope.launch{ getHistory().collectLatest{ setState{ copy(items = it) } } } }
    override fun onEvent(event: HistoryEvent){}
}
