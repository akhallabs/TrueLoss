package tm.trueloss.feature.result.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.trueloss.core.domain.model.TraceModel
interface ResultRepository { fun getResult(id: String): Flow<TraceModel?>; suspend fun shareAsImage(id: String): ByteArray }
