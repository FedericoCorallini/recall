package com.fcorallini.recall.core.domain.model

data class PdfSource(
    val id: String,
    val displayName: String,
    val uriString: String,
    val createdAtEpochMs: Long,
    val practiceCount: Int = 0,
    val lastPracticedEpochMs: Long? = null,
    val averageScore: Float = 0f  // 0.0 to 1.0
)
