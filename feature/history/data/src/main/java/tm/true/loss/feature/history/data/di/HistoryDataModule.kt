package tm.true.loss.feature.history.data.di
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tm.true.loss.feature.history.data.repository.HistoryRepositoryImpl
import tm.true.loss.feature.history.domain.repository.HistoryRepository
@Module @InstallIn(SingletonComponent::class)
abstract class HistoryDataModule{ @Binds abstract fun bind(r: HistoryRepositoryImpl): HistoryRepository }
