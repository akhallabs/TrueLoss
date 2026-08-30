package tm.trueloss.feature.history.domain.model
data class HistoryItem(val id: String, val target: String, val date: Long, val hopCount: Int, val avgLoss: Float)
