package com.fcorallini.recall.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.model.PdfSource
import com.fcorallini.recall.home.domain.usecase.GenerateFromPdfUseCase
import com.fcorallini.recall.home.domain.usecase.ObservePdfSourcesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Idle : HomeUiState()
    data object Loading : HomeUiState()
    data class Success(val sourceId: String) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateFromPdfUseCase: GenerateFromPdfUseCase,
    private val observePdfSourcesUseCase: ObservePdfSourcesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _pdfSources = MutableStateFlow<List<PdfSource>>(emptyList())
    val pdfSources: StateFlow<List<PdfSource>> = _pdfSources.asStateFlow()

    init {
        observePdfSources()
    }

    private fun observePdfSources() {
        viewModelScope.launch {
            observePdfSourcesUseCase().collect { sources ->
                _pdfSources.value = sources
            }
        }
    }

    fun generateFromPdf(uriString: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val result = generateFromPdfUseCase(uriString)) {
                is Result.Success -> {
                    _uiState.value = HomeUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = HomeUiState.Error(
                        result.exception.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = HomeUiState.Idle
    }
}
