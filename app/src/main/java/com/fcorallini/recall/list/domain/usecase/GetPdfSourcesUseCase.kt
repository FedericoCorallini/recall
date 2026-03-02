package com.fcorallini.recall.list.domain.usecase

import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPdfSourcesUseCase @Inject constructor(
    private val pdfSourceRepository: PdfSourceRepository
) {
    operator fun invoke(): Flow<List<PdfSource>> {
        return pdfSourceRepository.observeAll()
    }
}
