package tm.trueloss.core.domain.usecase
import kotlinx.coroutines.flow.Flow
abstract class UseCase<in P, R> { abstract suspend operator fun invoke(params: P): R }
abstract class FlowUseCase<in P, R> { abstract operator fun invoke(params: P): Flow<R> }
abstract class NoParamUseCase<R> { abstract suspend operator fun invoke(): R }
