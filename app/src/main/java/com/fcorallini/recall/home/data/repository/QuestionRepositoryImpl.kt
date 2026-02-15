package com.fcorallini.recall.home.data.repository

import com.fcorallini.recall.core.db.dao.QuestionDao
import com.fcorallini.recall.core.db.entity.toDomain
import com.fcorallini.recall.core.db.entity.toEntity
import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.home.data.openai.OpenAiQuestionGenerator
import com.fcorallini.recall.home.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val openAiQuestionGenerator: OpenAiQuestionGenerator
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
        sourceId: String
    ): List<Question> {
        return openAiQuestionGenerator.generateQuestionsFromPdf(pdfBytes, filename, sourceId)
    }
}
