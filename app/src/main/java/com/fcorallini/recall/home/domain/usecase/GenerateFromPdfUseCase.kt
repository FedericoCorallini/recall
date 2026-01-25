package com.fcorallini.recall.home.domain.usecase

import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.home.domain.repository.GenerationRepository
import javax.inject.Inject

class GenerateFromPdfUseCase @Inject constructor(
    private val repository: GenerationRepository
) {
    suspend operator fun invoke(uriString: String): Result<String> {
        return repository.generateFromPdf(uriString)
    }
}
