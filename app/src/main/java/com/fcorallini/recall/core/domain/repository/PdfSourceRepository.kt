package com.fcorallini.recall.core.domain.repository

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.model.PdfSource
import kotlinx.coroutines.flow.Flow

interface PdfSourceRepository {
    suspend fun insert(pdfSource: PdfSource)
    fun observeAll(): Flow<List<PdfSource>>
    suspend fun getById(id: String): PdfSource?
    suspend fun updatePdfSourceStats(sourceId: String, newScore: Float): Result<Unit>
    suspend fun deleteById(id: String): Result<Unit>
    suspend fun updateDisplayName(id: String, newDisplayName: String): Result<Unit>
}
