package com.fcorallini.recall.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fcorallini.recall.core.data.db.entity.PdfSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfSourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: PdfSourceEntity)

    @Update
    suspend fun update(source: PdfSourceEntity)

    @Query("SELECT * FROM pdf_sources WHERE id = :id")
    suspend fun getById(id: String): PdfSourceEntity?

    @Query("""
        SELECT * FROM pdf_sources
        ORDER BY
            lastPracticedEpochMs IS NOT NULL, 
            lastPracticedEpochMs ASC,
            averageScore ASC, 
            practiceCount ASC,
            createdAtEpochMs DESC
    """)
    fun observeAll(): Flow<List<PdfSourceEntity>>
}
