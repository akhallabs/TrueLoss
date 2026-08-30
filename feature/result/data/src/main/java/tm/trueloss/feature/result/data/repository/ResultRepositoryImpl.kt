package tm.trueloss.feature.result.data.repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import tm.trueloss.core.domain.model.TraceModel
import tm.trueloss.feature.result.domain.repository.ResultRepository
import javax.inject.Inject
class ResultRepositoryImpl @Inject constructor(): ResultRepository {
    override fun getResult(id: String): Flow<TraceModel?> = flowOf(null)
    override suspend fun shareAsImage(id: String): ByteArray = ByteArray(0)
}
