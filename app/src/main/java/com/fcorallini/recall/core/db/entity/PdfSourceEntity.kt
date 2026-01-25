package com.fcorallini.recall.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fcorallini.recall.core.model.PdfSource

@Entity(tableName = "pdf_sources")
data class PdfSourceEntity(
    @PrimaryKey
    val id: String,
    val displayName: String,
    val uriString: String,
    val createdAtEpochMs: Long,
    val practiceCount: Int = 0,
    val lastPracticedEpochMs: Long? = null,
    val averageScore: Float = 0f
)

fun PdfSourceEntity.toDomain(): PdfSource = PdfSource(
    id = id,
    displayName = displayName,
    uriString = uriString,
    createdAtEpochMs = createdAtEpochMs,
    practiceCount = practiceCount,
    lastPracticedEpochMs = lastPracticedEpochMs,
    averageScore = averageScore
)

fun PdfSource.toEntity(): PdfSourceEntity = PdfSourceEntity(
    id = id,
    displayName = displayName,
    uriString = uriString,
    createdAtEpochMs = createdAtEpochMs,
    practiceCount = practiceCount,
    lastPracticedEpochMs = lastPracticedEpochMs,
    averageScore = averageScore
)
