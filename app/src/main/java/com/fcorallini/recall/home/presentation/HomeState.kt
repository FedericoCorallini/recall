package com.fcorallini.recall.home.presentation

import com.fcorallini.recall.core.model.PdfSource

data class HomeState(
    val pdfSources: List<PdfSource> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToQuizId: String? = null
)
