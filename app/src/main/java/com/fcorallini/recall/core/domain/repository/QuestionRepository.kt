package com.fcorallini.recall.core.domain.repository

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun insertAll(questions: List<Question>)
    fun observeBySourceId(sourceId: String): Flow<List<Question>>

    suspend fun generateQuestionsFromPdf(
        pdfBytes: ByteArray,
        filename: String,
        sourceId: String,
        questionCount: Int
    ): List<Question>

    suspend fun submitAnswer(questionId: String, userAnswer: String): Result<Unit>
}
