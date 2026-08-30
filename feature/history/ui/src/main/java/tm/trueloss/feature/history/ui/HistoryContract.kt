package tm.trueloss.feature.history.ui
import tm.trueloss.feature.history.domain.model.HistoryItem
data class HistoryUiState(val items: List<HistoryItem> = emptyList(), val isLoading: Boolean = false)
sealed interface HistoryEvent { data class Delete(val id: String) : HistoryEvent; data object Clear : HistoryEvent; data class Open(val id: String) : HistoryEvent }
sealed interface HistoryEffect { data class OpenResult(val id: String) : HistoryEffect }
