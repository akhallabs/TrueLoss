package tm.true.loss.core.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "traces")
data class TraceEntity(
    @PrimaryKey val id: String,
    val target: String,
    val createdAt: Long
)
