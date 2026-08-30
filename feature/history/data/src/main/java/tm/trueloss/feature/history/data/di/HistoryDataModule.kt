package tm.trueloss.feature.history.data.di
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tm.trueloss.feature.history.data.repository.HistoryRepositoryImpl
import tm.trueloss.feature.history.domain.repository.HistoryRepository
@Module @InstallIn(SingletonComponent::class)
abstract class HistoryDataModule{ @Binds abstract fun bind(r: HistoryRepositoryImpl): HistoryRepository }
