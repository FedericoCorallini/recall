package com.fcorallini.recall.list.presentation

import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.model.PracticeSession

data class ListState(
    val pdfSources: List<PdfSource> = emptyList(),
    val selectedSourceId: String? = null,
    val practiceSessions: List<PracticeSession> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
