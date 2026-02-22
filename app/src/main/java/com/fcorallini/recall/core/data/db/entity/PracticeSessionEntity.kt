package com.fcorallini.recall.core.data.db.entity
 
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fcorallini.recall.core.domain.model.PracticeSession
 
@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceId: String,
    val completedAtEpochMs: Long,
    val score: Float,
    val correctCount: Int,
    val totalCount: Int
)
 
fun PracticeSessionEntity.toDomain(): PracticeSession = PracticeSession(
    id = id,
    sourceId = sourceId,
    completedAtEpochMs = completedAtEpochMs,
    score = score,
    correctCount = correctCount,
    totalCount = totalCount
)
 
fun PracticeSession.toEntity(): PracticeSessionEntity = PracticeSessionEntity(
    id = id,
    sourceId = sourceId,
    completedAtEpochMs = completedAtEpochMs,
    score = score,
    correctCount = correctCount,
    totalCount = totalCount
)