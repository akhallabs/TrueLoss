package tm.trueloss.feature.trace.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.trueloss.feature.trace.domain.model.TraceConfig
import tm.trueloss.feature.trace.domain.model.TraceHop
interface TraceRepository {
    fun trace(config: TraceConfig): Flow<List<TraceHop>>
    suspend fun resolveAsn(ip: String): String?
    suspend fun resolveGeo(ip: String): Pair<String?, String?> // country, city
}
