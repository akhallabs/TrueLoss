package tm.true.loss.core.database
import androidx.room.Database
import androidx.room.RoomDatabase
import tm.true.loss.core.database.entity.TraceEntity
@Database(entities = [TraceEntity::class], version = 1, exportSchema = false)
abstract class TrueLossDatabase : RoomDatabase()
