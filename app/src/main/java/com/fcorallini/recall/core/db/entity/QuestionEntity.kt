package com.fcorallini.recall.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.core.model.QuestionStats
import com.fcorallini.recall.core.model.QuestionType

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val sourceId: String,
    val type: QuestionType,
    val prompt: String,
    val options: List<String>,
    val answer: String,
    val totalTimesAsked: Int = 0,
    val totalSuccess: Int = 0,
    val lastTimeAskedEpochMs: Long? = null,
    val wasLastTimeSuccess: Boolean? = null,
    val rating: Float = 0f
)

fun QuestionEntity.toDomain(): Question = Question(
    id = id,
    sourceId = sourceId,
    type = type,
    prompt = prompt,
    options = options,
    answer = answer,
    stats = QuestionStats(
        totalTimesAsked = totalTimesAsked,
        totalSuccess = totalSuccess,
        lastTimeAskedEpochMs = lastTimeAskedEpochMs,
        wasLastTimeSuccess = wasLastTimeSuccess,
        rating = rating
    )
)

fun Question.toEntity(): QuestionEntity = QuestionEntity(
    id = id,
    sourceId = sourceId,
    type = type,
    prompt = prompt,
    options = options,
    answer = answer,
    totalTimesAsked = stats.totalTimesAsked,
    totalSuccess = stats.totalSuccess,
    lastTimeAskedEpochMs = stats.lastTimeAskedEpochMs,
    wasLastTimeSuccess = stats.wasLastTimeSuccess,
    rating = stats.rating
)
