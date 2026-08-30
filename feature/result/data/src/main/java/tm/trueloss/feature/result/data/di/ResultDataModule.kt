package tm.trueloss.feature.result.data.di
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tm.trueloss.feature.result.data.repository.ResultRepositoryImpl
import tm.trueloss.feature.result.domain.repository.ResultRepository
@Module @InstallIn(SingletonComponent::class)
abstract class ResultDataModule{ @Binds abstract fun bind(r: ResultRepositoryImpl): ResultRepository }
