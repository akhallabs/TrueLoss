package tm.trueloss.feature.result.ui
import tm.trueloss.core.domain.model.TraceModel
data class ResultUiState(val trace: TraceModel? = null, val isSharing: Boolean = false)
sealed interface ResultEvent{ data object Share: ResultEvent; data object Back: ResultEvent }
sealed interface ResultEffect{ data object Shared: ResultEffect }
