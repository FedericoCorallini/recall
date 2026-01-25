package com.fcorallini.recall.home.data.repository

import com.fcorallini.recall.core.common.DispatchersProvider
import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.common.TimeProvider
import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.dao.QuestionDao
import com.fcorallini.recall.core.db.entity.toEntity
import com.fcorallini.recall.core.model.PdfSource
import com.fcorallini.recall.home.data.generator.MockQuestionGenerator
import com.fcorallini.recall.home.domain.repository.GenerationRepository
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class GenerationRepositoryImpl @Inject constructor(
    private val pdfSourceDao: PdfSourceDao,
    private val questionDao: QuestionDao,
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatchersProvider
) : GenerationRepository {

    override suspend fun generateFromPdf(uriString: String): Result<String> = withContext(dispatchers.io) {
        try {
            val sourceId = UUID.randomUUID().toString()
            val displayName = extractDisplayName(uriString)
            
            // Create PdfSource
            val pdfSource = PdfSource(
                id = sourceId,
                displayName = displayName,
                uriString = uriString,
                createdAtEpochMs = timeProvider.currentTimeMillis()
            )
            
            // Generate mock questions
            val questions = MockQuestionGenerator.generateQuestions(sourceId)
            
            // Persist to database
            pdfSourceDao.insert(pdfSource.toEntity())
            questionDao.insertAll(questions.map { it.toEntity() })
            
            Result.Success(sourceId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun extractDisplayName(uriString: String): String {
        // Extract filename from URI
        return uriString.substringAfterLast("/", "Unknown PDF")
    }
}
