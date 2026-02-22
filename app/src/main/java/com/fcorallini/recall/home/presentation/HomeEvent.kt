package com.fcorallini.recall.home.presentation

sealed interface HomeEvent {
    data class GenerateFromPdf(val uri: String) : HomeEvent
    data class DeletePdfSource(val sourceId: String) : HomeEvent
    data class RenamePdfSource(val sourceId: String, val newDisplayName: String) : HomeEvent
    data object ResetState : HomeEvent
}