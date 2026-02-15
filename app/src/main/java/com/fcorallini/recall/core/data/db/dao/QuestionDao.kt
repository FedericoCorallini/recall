package com.fcorallini.recall.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fcorallini.recall.core.data.db.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Update
    suspend fun update(question: QuestionEntity)

    @Query("""
        SELECT * FROM questions
        WHERE sourceId = :sourceId
        ORDER BY
            lastTimeAskedEpochMs IS NOT NULL,
            lastTimeAskedEpochMs ASC,
            rating DESC
        LIMIT 10
    """)
    fun getBySourceId(sourceId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: String): QuestionEntity?
}
