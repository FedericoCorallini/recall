package com.fcorallini.recall.home.presentation

import com.fcorallini.recall.core.domain.model.GlobalStats
import com.fcorallini.recall.core.domain.model.PdfSource

data class HomeState(
    val pdfSources: List<PdfSource> = emptyList(),
    val globalStats: GlobalStats = GlobalStats(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f, // 0.0 to 1.0
    val errorMessage: String? = null,
    val navigateToQuizId: String? = null
)
