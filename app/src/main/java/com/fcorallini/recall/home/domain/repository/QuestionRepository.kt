package com.fcorallini.recall.home.domain.repository

import com.fcorallini.recall.core.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun insertAll(questions: List<Question>)
    fun observeBySourceId(sourceId: String): Flow<List<Question>>

    suspend fun generateQuestionsFromPdf(
        pdfBytes: ByteArray,
        filename: String,
        sourceId: String
    ): List<Question>
}
