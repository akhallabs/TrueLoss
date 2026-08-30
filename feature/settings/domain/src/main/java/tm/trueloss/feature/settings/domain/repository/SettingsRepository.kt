package tm.trueloss.feature.settings.domain.repository
import kotlinx.coroutines.flow.Flow
import tm.trueloss.feature.settings.domain.model.SettingsModel
interface SettingsRepository{ fun settings(): Flow<SettingsModel>; suspend fun update(s: SettingsModel) }
