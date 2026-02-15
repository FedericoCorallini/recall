package com.fcorallini.recall.home.data.repository

import com.fcorallini.recall.core.common.DispatchersProvider
import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.common.TimeProvider
import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.entity.toDomain
import com.fcorallini.recall.core.db.entity.toEntity
import com.fcorallini.recall.core.model.PdfSource
import com.fcorallini.recall.home.domain.repository.PdfSourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PdfSourceRepositoryImpl @Inject constructor(
    private val pdfSourceDao: PdfSourceDao,
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

    override suspend fun updatePdfSourceStats(sourceId: String, newScore: Float): Result<Unit> =
        withContext(dispatchers.io) {
            try {
                val source = pdfSourceDao.getById(sourceId)
                    ?: return@withContext Result.Error(Exception("PDF Source not found"))

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

                pdfSourceDao.update(updatedSource)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
