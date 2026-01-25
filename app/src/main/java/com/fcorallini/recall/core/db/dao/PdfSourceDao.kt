package com.fcorallini.recall.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fcorallini.recall.core.db.entity.PdfSourceEntity

@Dao
interface PdfSourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(source: PdfSourceEntity)

    @Query("SELECT * FROM pdf_sources WHERE id = :id")
    suspend fun getById(id: String): PdfSourceEntity?

    @Query("SELECT * FROM pdf_sources ORDER BY createdAtEpochMs DESC")
    suspend fun getAll(): List<PdfSourceEntity>
}
