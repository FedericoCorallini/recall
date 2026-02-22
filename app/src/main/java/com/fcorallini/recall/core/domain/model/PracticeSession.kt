package com.fcorallini.recall.core.domain.model
 
data class PracticeSession(
    val id: Long = 0,
    val sourceId: String,
    val completedAtEpochMs: Long,
    val score: Float,
    val correctCount: Int,
    val totalCount: Int
)