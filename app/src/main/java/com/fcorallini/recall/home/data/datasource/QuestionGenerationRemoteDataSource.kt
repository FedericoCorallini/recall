package com.fcorallini.recall.home.data.datasource

import com.fcorallini.recall.core.model.Question

/**
 * Remote data source for generating questions from PDF files
 */
interface QuestionGenerationRemoteDataSource {
    /**
     * Generates questions from a PDF file
     * @param pdfBytes The PDF file bytes
     * @param filename The name of the PDF file
     * @param sourceId The ID of the PdfSource
     * @return List of generated questions
     * @throws Exception if generation fails
     */
    suspend fun generateQuestionsFromPdf(
        pdfBytes: ByteArray,
        filename: String,
        sourceId: String
    ): List<Question>
}
