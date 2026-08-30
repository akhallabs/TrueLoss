package tm.true.loss.feature.trace.data.di
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tm.true.loss.feature.trace.data.repository.TraceRepositoryImpl
import tm.true.loss.feature.trace.domain.repository.TraceRepository
@Module @InstallIn(SingletonComponent::class)
abstract class TraceDataModule { @Binds abstract fun bindTraceRepo(impl: TraceRepositoryImpl): TraceRepository }
