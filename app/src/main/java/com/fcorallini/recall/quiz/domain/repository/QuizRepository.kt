package com.fcorallini.recall.quiz.domain.repository

import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.model.Question
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun observeQuestionsBySourceId(sourceId: String): Flow<List<Question>>
    suspend fun submitAnswer(questionId: String, userAnswer: String): Result<Unit>
}
