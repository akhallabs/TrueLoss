package tm.true.loss.feature.trace.domain.usecase
import kotlinx.coroutines.flow.Flow
import tm.true.loss.feature.trace.domain.model.TraceConfig
import tm.true.loss.feature.trace.domain.model.TraceHop
import tm.true.loss.feature.trace.domain.repository.TraceRepository
import javax.inject.Inject
class StartTraceUseCase @Inject constructor(private val repo: TraceRepository){
    operator fun invoke(config: TraceConfig): Flow<List<TraceHop>> = repo.trace(config)
}
