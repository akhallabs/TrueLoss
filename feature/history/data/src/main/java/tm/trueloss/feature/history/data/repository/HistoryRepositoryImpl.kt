package tm.trueloss.feature.history.data.repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import tm.trueloss.feature.history.domain.model.HistoryItem
import tm.trueloss.feature.history.domain.repository.HistoryRepository
import javax.inject.Inject
class HistoryRepositoryImpl @Inject constructor(): HistoryRepository {
    override fun getHistory(): Flow<List<HistoryItem>> = flowOf(emptyList())
    override suspend fun delete(id: String){}
    override suspend fun clear(){}
}
