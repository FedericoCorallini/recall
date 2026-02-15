package com.fcorallini.recall.home.domain.usecase

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.data.common.TimeProvider
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.home.domain.extractor.PdfContentExtractor
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import com.fcorallini.recall.core.domain.repository.QuestionRepository
import java.util.UUID
import javax.inject.Inject

class GenerateQuizFromPdfUseCase @Inject constructor(
    private val pdfContentExtractor: PdfContentExtractor,
    private val pdfSourceRepository: PdfSourceRepository,
    private val questionRepository: QuestionRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(uriString: String): Result<String> {
        return try {

            val displayName = pdfContentExtractor.extractDisplayName(uriString)
            val pdfBytes = pdfContentExtractor.extractBytes(uriString)

            val sourceId = UUID.randomUUID().toString()

            val pdfSource = PdfSource(
                id = sourceId,
                displayName = displayName,
                uriString = uriString,
                createdAtEpochMs = timeProvider.currentTimeMillis()
            )

            val questions = questionRepository.generateQuestionsFromPdf(
                pdfBytes = pdfBytes,
                filename = displayName,
                sourceId = sourceId
            )

            pdfSourceRepository.insert(pdfSource)
            questionRepository.insertAll(questions)

            Result.Success(sourceId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
