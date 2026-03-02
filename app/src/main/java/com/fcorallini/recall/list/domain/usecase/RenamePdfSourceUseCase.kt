package com.fcorallini.recall.list.domain.usecase

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import javax.inject.Inject

class RenamePdfSourceUseCase @Inject constructor(
    private val pdfSourceRepository: PdfSourceRepository
) {
    suspend operator fun invoke(sourceId: String, newDisplayName: String): Result<Unit> {
        return pdfSourceRepository.updateDisplayName(sourceId, newDisplayName)
    }
}
