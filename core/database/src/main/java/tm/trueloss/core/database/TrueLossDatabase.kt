package tm.trueloss.core.database
import androidx.room.Database
import androidx.room.RoomDatabase
import tm.trueloss.core.database.entity.TraceEntity
@Database(entities = [TraceEntity::class], version = 1, exportSchema = false)
abstract class TrueLossDatabase : RoomDatabase()
