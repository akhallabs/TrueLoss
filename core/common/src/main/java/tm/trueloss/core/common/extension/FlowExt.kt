package tm.trueloss.core.common.extension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import tm.trueloss.core.common.result.Result
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map { Result.Success(it) as Result<T> }.catch { emit(Result.Error(it)) }
