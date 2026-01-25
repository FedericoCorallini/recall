package com.fcorallini.recall.quiz.data.repository

import com.fcorallini.recall.core.common.DispatchersProvider
import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.common.TimeProvider
import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.dao.QuestionDao
import com.fcorallini.recall.core.db.entity.toDomain
import com.fcorallini.recall.core.db.entity.toEntity
import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.core.model.QuestionStats
import com.fcorallini.recall.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val pdfSourceDao: PdfSourceDao,
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatchersProvider
) : QuizRepository {

    override fun observeQuestionsBySourceId(sourceId: String): Flow<List<Question>> {
        return questionDao.getBySourceId(sourceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun submitAnswer(questionId: String, userAnswer: String): Result<Unit> = 
        withContext(dispatchers.io) {
            try {
                val questionEntity = questionDao.getById(questionId)
                    ?: return@withContext Result.Error(Exception("Question not found"))

                val question = questionEntity.toDomain()
                val isCorrect = userAnswer.trim().equals(question.answer.trim(), ignoreCase = true)

                val updatedStats = QuestionStats(
                    totalTimesAsked = question.stats.totalTimesAsked + 1,
                    totalSuccess = question.stats.totalSuccess + if (isCorrect) 1 else 0,
                    lastTimeAskedEpochMs = timeProvider.currentTimeMillis(),
                    wasLastTimeSuccess = isCorrect,
                    rating = calculateRating(
                        totalSuccess = question.stats.totalSuccess + if (isCorrect) 1 else 0,
                        totalTimesAsked = question.stats.totalTimesAsked + 1
                    )
                )

                val updatedQuestion = question.copy(stats = updatedStats)
                questionDao.update(updatedQuestion.toEntity())

                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
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

    private fun calculateRating(totalSuccess: Int, totalTimesAsked: Int): Float {
        if (totalTimesAsked == 0) return 0f
        return totalSuccess.toFloat() / totalTimesAsked.toFloat()
    }
}
