package com.fcorallini.recall.list.presentation

sealed interface ListEvent {
    data class DeletePdfSource(val sourceId: String) : ListEvent
    data class RenamePdfSource(val sourceId: String, val newDisplayName: String) : ListEvent
    data object ResetError : ListEvent
}
