package com.fcorallini.recall.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.home.domain.usecase.GenerateQuizFromPdfUseCase
import com.fcorallini.recall.home.domain.usecase.ObservePdfSourcesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateQuizFromPdfUseCase: GenerateQuizFromPdfUseCase,
    private val observePdfSourcesUseCase: ObservePdfSourcesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observePdfSources()
    }

    private fun observePdfSources() {
        viewModelScope.launch {
            observePdfSourcesUseCase().collect { sources ->
                _state.update { it.copy(pdfSources = sources) }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.GenerateFromPdf -> generateFromPdf(event.uri)
            is HomeEvent.ResetState -> _state.update {
                it.copy(errorMessage = null, navigateToQuizId = null)
            }
        }
    }

    private fun generateFromPdf(uri: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = generateQuizFromPdfUseCase(uri)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(isLoading = false, navigateToQuizId = result.data)
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}
