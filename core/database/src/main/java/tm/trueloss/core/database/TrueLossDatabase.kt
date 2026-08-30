package tm.trueloss.core.database
import androidx.room.Database
import androidx.room.RoomDatabase
import tm.trueloss.core.database.dao.HistoryDao
import tm.trueloss.core.database.entity.HistoryEntity
import tm.trueloss.core.database.entity.TraceEntity
@Database(entities = [TraceEntity::class, HistoryEntity::class], version = 2, exportSchema = false)
abstract class TrueLossDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
