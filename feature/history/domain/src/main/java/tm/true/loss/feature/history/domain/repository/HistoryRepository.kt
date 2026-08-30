package tm.true.loss.feature.history.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.true.loss.feature.history.domain.model.HistoryItem
interface HistoryRepository { fun getHistory(): Flow<List<HistoryItem>>; suspend fun delete(id: String); suspend fun clear() }
