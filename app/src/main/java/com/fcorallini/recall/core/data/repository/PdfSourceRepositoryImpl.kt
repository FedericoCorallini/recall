package com.fcorallini.recall.core.data.repository

import com.fcorallini.recall.core.data.common.DispatchersProvider
import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.data.common.TimeProvider
import com.fcorallini.recall.core.data.db.dao.PdfSourceDao
import com.fcorallini.recall.core.data.db.dao.PracticeSessionDao
import com.fcorallini.recall.core.data.db.entity.PracticeSessionEntity
import com.fcorallini.recall.core.data.db.entity.toDomain
import com.fcorallini.recall.core.data.db.entity.toEntity
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PdfSourceRepositoryImpl @Inject constructor(
    private val pdfSourceDao: PdfSourceDao,
    private val practiceSessionDao: PracticeSessionDao,
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatchersProvider
) : PdfSourceRepository {

    override suspend fun insert(pdfSource: PdfSource) {
        pdfSourceDao.insert(pdfSource.toEntity())
    }

    override fun observeAll(): Flow<List<PdfSource>> {
        return pdfSourceDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: String): PdfSource? {
        return pdfSourceDao.getById(id)?.toDomain()
    }

    override suspend fun updatePdfSourceStats(
        sourceId: String,
        correctCount: Int,
        totalCount: Int
    ): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val source = pdfSourceDao.getById(sourceId)
                    ?: return@withContext Result.Error(Exception("PDF Source not found"))
 
                val newScore = if (totalCount > 0) {
                    correctCount.toFloat() / totalCount.toFloat()
                } else {
                    0f
                }

                val updatedPracticeCount = source.practiceCount + 1
                val updatedAverageScore = if (source.practiceCount == 0) {
                    newScore
                } else {
                    // Calculate new average
                    (source.averageScore * source.practiceCount + newScore) / updatedPracticeCount
                }

                val updatedSource = source.copy(
                    practiceCount = updatedPracticeCount,
                    lastPracticedEpochMs = timeProvider.currentTimeMillis(),
                    averageScore = updatedAverageScore
                )
 
                practiceSessionDao.insert(
                    PracticeSessionEntity(
                        sourceId = sourceId,
                        completedAtEpochMs = timeProvider.currentTimeMillis(),
                        score = newScore,
                        correctCount = correctCount,
                        totalCount = totalCount
                    )
                )

                pdfSourceDao.update(updatedSource)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteById(id: String): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val rows = pdfSourceDao.deleteById(id)
                if (rows == 0) {
                    Result.Error(Exception("PDF Source not found"))
                } else {
                    Result.Success(Unit)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun updateDisplayName(id: String, newDisplayName: String): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val rows = pdfSourceDao.updateDisplayName(id, newDisplayName)
                if (rows == 0) {
                    Result.Error(Exception("PDF Source not found"))
                } else {
                    Result.Success(Unit)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
