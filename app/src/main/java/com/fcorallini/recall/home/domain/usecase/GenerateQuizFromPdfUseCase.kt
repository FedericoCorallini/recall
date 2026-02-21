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
    companion object {
        // OpenAI limit for PDF input is ~50MB, we use 45MB to be safe
        private const val MAX_PDF_SIZE_BYTES = 40L * 1024 * 1024
        private const val MAX_QUESTIONS = 100
        private const val MIN_QUESTIONS = 10
        private const val PAGES_PER_QUESTION = 1f
    }

    suspend operator fun invoke(uriString: String): Result<String> {
        return try {
            val displayName = pdfContentExtractor.extractDisplayName(uriString)
            val pdfBytes = pdfContentExtractor.extractBytes(uriString)

            // Validate PDF size
            if (pdfBytes.size > MAX_PDF_SIZE_BYTES) {
                val maxMB = MAX_PDF_SIZE_BYTES / (1024 * 1024)
                return Result.Error(
                    Exception("PDF demasiado grande. Máximo permitido: ${maxMB}MB. " +
                             "Por favor, selecciona un archivo más pequeño o divide el contenido.")
                )
            }

            // Get page count and calculate questions
            val pageCount = pdfContentExtractor.getPageCount(uriString)
            val questionCount = calculateQuestionCount(pageCount)

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
                sourceId = sourceId,
                questionCount = questionCount
            )

            pdfSourceRepository.insert(pdfSource)
            questionRepository.insertAll(questions)

            Result.Success(sourceId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Calculates the number of questions based on page count.
     * Formula: (pageCount / PAGES_PER_QUESTION), clamped between MIN_QUESTIONS and MAX_QUESTIONS
     * 
     * Examples:
     * - 50 pages → ~14 questions
     * - 200 pages → ~57 questions  
     * - 800 pages → 100 questions (capped at max)
     */
    private fun calculateQuestionCount(pageCount: Int): Int {
        val calculated = (pageCount / PAGES_PER_QUESTION).toInt()
        return calculated.coerceIn(MIN_QUESTIONS, MAX_QUESTIONS)
    }
}
