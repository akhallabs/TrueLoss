package tm.true.loss.feature.trace.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.true.loss.feature.trace.domain.model.TraceConfig
import tm.true.loss.feature.trace.domain.model.TraceHop
interface TraceRepository {
    fun trace(config: TraceConfig): Flow<List<TraceHop>>
    suspend fun resolveAsn(ip: String): String?
    suspend fun resolveGeo(ip: String): Pair<String?, String?> // country, city
}
