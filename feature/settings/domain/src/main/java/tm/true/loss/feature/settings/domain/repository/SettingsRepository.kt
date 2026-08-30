package tm.true.loss.feature.settings.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.true.loss.feature.settings.domain.model.SettingsModel
interface SettingsRepository{ fun settings(): Flow<SettingsModel>; suspend fun update(s: SettingsModel) }
