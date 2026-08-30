package tm.trueloss.feature.history.domain.usecase
import tm.trueloss.feature.history.domain.repository.HistoryRepository
import javax.inject.Inject
class GetHistoryUseCase @Inject constructor(private val repo: HistoryRepository){ operator fun invoke() = repo.getHistory() }
