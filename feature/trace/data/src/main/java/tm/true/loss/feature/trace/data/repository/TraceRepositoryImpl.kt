package tm.true.loss.feature.trace.data.repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tm.true.loss.feature.trace.domain.model.TraceConfig
import tm.true.loss.feature.trace.domain.model.TraceHop
import tm.true.loss.feature.trace.domain.repository.TraceRepository
import javax.inject.Inject
class TraceRepositoryImpl @Inject constructor(): TraceRepository {
    override fun trace(config: TraceConfig): Flow<List<TraceHop>> = flow {
        // TODO: ICMP/UDP/TCP traceroute - native ping + ASN/Geo lookup
        emit(emptyList())
    }
    override suspend fun resolveAsn(ip: String): String? = null
    override suspend fun resolveGeo(ip: String): Pair<String?, String?> = null to null
}
