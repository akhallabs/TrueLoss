package tm.true.loss.feature.result.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.true.loss.core.domain.model.TraceModel
interface ResultRepository { fun getResult(id: String): Flow<TraceModel?>; suspend fun shareAsImage(id: String): ByteArray }
