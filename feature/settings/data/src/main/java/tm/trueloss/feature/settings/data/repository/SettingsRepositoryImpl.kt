package tm.trueloss.feature.settings.data.repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import tm.trueloss.feature.settings.domain.model.SettingsModel
import tm.trueloss.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject
class SettingsRepositoryImpl @Inject constructor(): SettingsRepository{
    override fun settings(): Flow<SettingsModel> = flowOf(SettingsModel())
    override suspend fun update(s: SettingsModel){}
}
