package tm.trueloss.core.database.di
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tm.trueloss.core.database.TrueLossDatabase
import tm.trueloss.core.database.dao.HistoryDao
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton fun provideDb(@ApplicationContext ctx: Context): TrueLossDatabase =
        Room.databaseBuilder(ctx, TrueLossDatabase::class.java, "trueloss.db").fallbackToDestructiveMigration().build()
    @Provides fun provideHistoryDao(db: TrueLossDatabase): HistoryDao = db.historyDao()
}
