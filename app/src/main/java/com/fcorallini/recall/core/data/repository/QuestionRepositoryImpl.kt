package com.fcorallini.recall.core.data.repository

import com.fcorallini.recall.core.data.common.DispatchersProvider
import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.data.common.TimeProvider
import com.fcorallini.recall.core.data.db.dao.QuestionDao
import com.fcorallini.recall.core.data.db.entity.toDomain
import com.fcorallini.recall.core.data.db.entity.toEntity
import com.fcorallini.recall.core.domain.model.Question
import com.fcorallini.recall.core.domain.model.QuestionStats
import com.fcorallini.recall.home.data.openai.OpenAiQuestionGenerator
import com.fcorallini.recall.core.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val openAiQuestionGenerator: OpenAiQuestionGenerator,
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatchersProvider
) : QuestionRepository {

    override suspend fun insertAll(questions: List<Question>) {
        questionDao.insertAll(questions.map { it.toEntity() })
    }

    override fun observeBySourceId(sourceId: String): Flow<List<Question>> {
        return questionDao.getBySourceId(sourceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun generateQuestionsFromPdf(
        pdfBytes: ByteArray,
        filename: String,
        sourceId: String,
        questionCount: Int
    ): List<Question> {
        return openAiQuestionGenerator.generateQuestionsFromPdf(
            pdfBytes = pdfBytes, 
            filename = filename, 
            sourceId = sourceId,
            questionCount = questionCount
        )
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

    private fun calculateRating(totalSuccess: Int, totalTimesAsked: Int): Float {
        if (totalTimesAsked == 0) return 0f
        return totalSuccess.toFloat() / totalTimesAsked.toFloat()
    }
}
