package tm.trueloss.feature.history.data.repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tm.trueloss.core.database.dao.HistoryDao
import tm.trueloss.core.database.entity.HistoryEntity
import tm.trueloss.feature.history.domain.model.HistoryItem
import tm.trueloss.feature.history.domain.repository.HistoryRepository
import javax.inject.Inject
class HistoryRepositoryImpl @Inject constructor(private val dao: HistoryDao) : HistoryRepository {
    override fun getHistory(): Flow<List<HistoryItem>> = dao.observeAll().map { list -> list.map { HistoryItem(id = it.id, target = it.target, date = it.date, hopCount = it.hopCount, avgLoss = it.avgLoss) } }
    override suspend fun delete(id: String) = dao.delete(id)
    override suspend fun clear() = dao.clear()
}
