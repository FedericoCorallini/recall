package com.fcorallini.recall.core.data.db.dao
 
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fcorallini.recall.core.data.db.entity.PracticeSessionEntity
import kotlinx.coroutines.flow.Flow
 
@Dao
interface PracticeSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PracticeSessionEntity)
 
    @Query("""
        SELECT * FROM practice_sessions
        ORDER BY completedAtEpochMs DESC
    """)
    fun observeAll(): Flow<List<PracticeSessionEntity>>
}