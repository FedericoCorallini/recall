package com.fcorallini.recall.generation.presentation

import com.fcorallini.recall.home.presentation.HomeEvent

interface GenerationEvent {
    data class GenerateFromPdf(val uri: String) : GenerationEvent
    data object ResetState : GenerationEvent
}