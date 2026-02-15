package com.fcorallini.recall.home.presentation

interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Success(val sourceId: String) : HomeUiState
    data class Error(val message: String) : HomeUiState
}