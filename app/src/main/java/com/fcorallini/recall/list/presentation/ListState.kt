package com.fcorallini.recall.list.presentation

import com.fcorallini.recall.core.domain.model.PdfSource

data class ListState(
    val pdfSources: List<PdfSource> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
