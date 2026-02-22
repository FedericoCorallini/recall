package com.fcorallini.recall.core.domain.model
 
data class GlobalStats(
    val streakDays: Int = 0,
    val totalPractices: Int = 0,
    val averageScore: Float = 0f,
    val lastPracticedEpochMs: Long? = null
)