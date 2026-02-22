package com.fcorallini.recall.quiz.domain.usecase

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import javax.inject.Inject

class UpdatePdfSourceStatsUseCase @Inject constructor(
    private val pdfSourceRepository: PdfSourceRepository
) {
    suspend operator fun invoke(
        sourceId: String,
        correctCount: Int,
        totalCount: Int
    ): Result<Unit> {
        return pdfSourceRepository.updatePdfSourceStats(
            sourceId = sourceId,
            correctCount = correctCount,
            totalCount = totalCount
        )
    }
}
