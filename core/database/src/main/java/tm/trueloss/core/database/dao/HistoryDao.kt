package tm.trueloss.core.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tm.trueloss.core.database.entity.HistoryEntity
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY date DESC")
    fun observeAll(): Flow<List<HistoryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity)
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun delete(id: String)
    @Query("DELETE FROM history")
    suspend fun clear()
}
