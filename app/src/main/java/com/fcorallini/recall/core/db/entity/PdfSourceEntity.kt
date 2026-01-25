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
    val createdAtEpochMs: Long
)

fun PdfSourceEntity.toDomain(): PdfSource = PdfSource(
    id = id,
    displayName = displayName,
    uriString = uriString,
    createdAtEpochMs = createdAtEpochMs
)

fun PdfSource.toEntity(): PdfSourceEntity = PdfSourceEntity(
    id = id,
    displayName = displayName,
    uriString = uriString,
    createdAtEpochMs = createdAtEpochMs
)
