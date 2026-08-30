package tm.trueloss.core.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val target: String,
    val date: Long,
    val hopCount: Int,
    val avgLoss: Float,
    val protocol: String
)
