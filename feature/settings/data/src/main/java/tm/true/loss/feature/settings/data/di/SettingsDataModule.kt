package tm.true.loss.feature.settings.data.di
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tm.true.loss.feature.settings.data.repository.SettingsRepositoryImpl
import tm.true.loss.feature.settings.domain.repository.SettingsRepository
@Module @InstallIn(SingletonComponent::class)
abstract class SettingsDataModule{ @Binds abstract fun bind(r: SettingsRepositoryImpl): SettingsRepository }
