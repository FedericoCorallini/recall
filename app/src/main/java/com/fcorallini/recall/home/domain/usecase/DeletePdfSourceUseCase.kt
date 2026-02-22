package com.fcorallini.recall.home.domain.usecase

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import javax.inject.Inject

class DeletePdfSourceUseCase @Inject constructor(
    private val pdfSourceRepository: PdfSourceRepository
) {
    suspend operator fun invoke(sourceId: String): Result<Unit> {
        return pdfSourceRepository.deleteById(sourceId)
    }
}