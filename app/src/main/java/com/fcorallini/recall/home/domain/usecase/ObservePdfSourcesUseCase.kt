package com.fcorallini.recall.home.domain.usecase

import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePdfSourcesUseCase @Inject constructor(
    private val pdfSourceRepository: PdfSourceRepository
) {
    operator fun invoke(): Flow<List<PdfSource>> {
        return pdfSourceRepository.observeAll()
    }
}
