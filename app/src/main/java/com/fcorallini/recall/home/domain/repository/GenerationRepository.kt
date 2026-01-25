package com.fcorallini.recall.home.domain.repository

import com.fcorallini.recall.core.common.Result

interface GenerationRepository {
    suspend fun generateFromPdf(uriString: String): Result<String>  // Returns sourceId
}
