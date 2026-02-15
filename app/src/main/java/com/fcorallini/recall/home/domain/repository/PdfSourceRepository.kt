package com.fcorallini.recall.home.domain.repository

import com.fcorallini.recall.core.model.PdfSource
import kotlinx.coroutines.flow.Flow

interface PdfSourceRepository {
    suspend fun insert(pdfSource: PdfSource)
    fun observeAll(): Flow<List<PdfSource>>
    suspend fun getById(id: String): PdfSource?
}
