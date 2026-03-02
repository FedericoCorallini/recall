package com.fcorallini.recall.core.data.repository
 
import com.fcorallini.recall.core.data.db.dao.PracticeSessionDao
import com.fcorallini.recall.core.data.db.entity.toDomain
import com.fcorallini.recall.core.data.db.entity.toEntity
import com.fcorallini.recall.core.domain.model.PracticeSession
import com.fcorallini.recall.core.domain.repository.PracticeSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
 
class PracticeSessionRepositoryImpl @Inject constructor(
    private val practiceSessionDao: PracticeSessionDao
) : PracticeSessionRepository {
    override suspend fun insert(session: PracticeSession) {
        practiceSessionDao.insert(session.toEntity())
    }

    override fun observeAll(): Flow<List<PracticeSession>> {
        return practiceSessionDao.observeAll().map { sessions ->
            sessions.map { it.toDomain() }
        }
    }

    override fun observeBySourceId(sourceId: String): Flow<List<PracticeSession>> {
        return practiceSessionDao.observeBySourceId(sourceId).map { sessions ->
            sessions.map { it.toDomain() }
        }
    }
}