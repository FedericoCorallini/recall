package com.fcorallini.recall.home.presentation

sealed interface HomeEvent {
    data class GenerateFromPdf(val uri: String) : HomeEvent
    data object ResetState : HomeEvent
}