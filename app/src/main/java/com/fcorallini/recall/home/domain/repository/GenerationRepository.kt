package com.fcorallini.recall.home.domain.repository

import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.model.PdfSource
import kotlinx.coroutines.flow.Flow

interface GenerationRepository {
    suspend fun generateFromPdf(uriString: String): Result<String>  // Returns sourceId
    fun observeAllPdfSources(): Flow<List<PdfSource>>
}
