package com.fcorallini.recall.core.domain.repository
 
import com.fcorallini.recall.core.domain.model.PracticeSession
import kotlinx.coroutines.flow.Flow
 
interface PracticeSessionRepository {
    suspend fun insert(session: PracticeSession)
    fun observeAll(): Flow<List<PracticeSession>>
    fun observeBySourceId(sourceId: String): Flow<List<PracticeSession>>
}