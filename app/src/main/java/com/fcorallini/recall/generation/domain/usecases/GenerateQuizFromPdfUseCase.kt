package com.fcorallini.recall.generation.domain.usecases

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.data.common.TimeProvider
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import com.fcorallini.recall.core.domain.repository.QuestionRepository
import com.fcorallini.recall.generation.domain.extractor.PdfContentExtractor
import java.util.UUID
import javax.inject.Inject
import kotlin.math.min

class GenerateQuizFromPdfUseCase @Inject constructor(
    private val pdfContentExtractor: PdfContentExtractor,
    private val pdfSourceRepository: PdfSourceRepository,
    private val questionRepository: QuestionRepository,
    private val timeProvider: TimeProvider
) {
    companion object {
        private const val MAX_PDF_SIZE_BYTES = 25L * 1024 * 1024
        private const val MAX_PDF_PAGES = 50
        private const val MAX_QUESTIONS = 40
        private const val MIN_QUESTIONS = 5
        private const val MIN_WORDS_FOR_QUIZ = 50
    }

    suspend operator fun invoke(uriString: String): Result<String> {
        return try {
            val displayName = pdfContentExtractor.extractDisplayName(uriString)
            val pdfBytes = pdfContentExtractor.extractBytes(uriString)

            // Validate PDF size
            if (pdfBytes.size > MAX_PDF_SIZE_BYTES) {
                val maxMB = MAX_PDF_SIZE_BYTES / (1024 * 1024)
                return Result.Error(
                    Exception(
                        "PDF is too large. Maximum allowed: ${maxMB}MB. " +
                            "Please select a smaller file or split the content."
                    )
                )
            }

            // Get page count
            val pageCount = pdfContentExtractor.getPageCount(uriString)
            if (pageCount > MAX_PDF_PAGES) {
                return Result.Error(
                    Exception(
                        "PDF is too long. Maximum allowed: $MAX_PDF_PAGES pages. " +
                            "Please split the document."
                    )
                )
            }

            // Extract and validate text content
            val wordCount = pdfContentExtractor.contWords(uriString)

            if (wordCount < MIN_WORDS_FOR_QUIZ) {
                return Result.Error(
                    Exception(
                        "This PDF does not contain enough readable text to generate a useful quiz. " +
                            "Found $wordCount words, minimum required: $MIN_WORDS_FOR_QUIZ. " +
                            "The PDF might be scanned images or have text as images."
                    )
                )
            }

            // Calculate question count based on text density and pages
            val questionCount = calculateQuestionCount(pageCount, wordCount)

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

    private fun calculateQuestionCount(pageCount: Int, wordCount: Int): Int {

        val wordsPerPage = if (pageCount > 0) wordCount / pageCount else 0

        val byWords = wordCount / 125
        val byPages = (pageCount * 1.25).toInt()

        val estimate =
            if (wordsPerPage < 40) {
                byPages
            } else {
                min(byWords, byPages)
            }

        return estimate.coerceIn(MIN_QUESTIONS, MAX_QUESTIONS)
    }
}