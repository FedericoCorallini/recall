package com.fcorallini.recall.home.domain.usecase

import com.fcorallini.recall.core.model.PdfSource
import com.fcorallini.recall.home.domain.repository.PdfSourceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePdfSourcesUseCase @Inject constructor(
    private val pdfSourceRepository: PdfSourceRepository
) {
    operator fun invoke(): Flow<List<PdfSource>> {
        return pdfSourceRepository.observeAll()
    }
}
