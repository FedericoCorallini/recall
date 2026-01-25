package com.fcorallini.recall.core.model

data class QuestionStats(
    val totalTimesAsked: Int = 0,
    val totalSuccess: Int = 0,
    val lastTimeAskedEpochMs: Long? = null,
    val wasLastTimeSuccess: Boolean? = null,
    val rating: Float = 0f
)
