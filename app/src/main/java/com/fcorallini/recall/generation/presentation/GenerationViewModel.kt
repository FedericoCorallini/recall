package com.fcorallini.recall.generation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.generation.domain.usecases.GenerateQuizFromPdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerationViewModel @Inject constructor(
    private val generateQuizFromPdfUseCase: GenerateQuizFromPdfUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GenerationState())
    val state: StateFlow<GenerationState> = _state.asStateFlow()

    fun onEvent(event: GenerationEvent) {
        when (event) {
            is GenerationEvent.GenerateFromPdf -> generateFromPdf(event.uri)
            is GenerationEvent.ResetState -> _state.update {
                it.copy(errorMessage = null, navigateToQuizId = null)
            }
        }

    }

    private fun generateFromPdf(uri: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loadingProgress = 0f, errorMessage = null) }

            // Animate progress from 0 to 0.9 over ~90 seconds
            val progressDuration = 90000L // 90 seconds
            val startTime = System.currentTimeMillis()

            // Launch concurrent job to animate progress while quiz is generating
            val progressJob = launch {
                while (true) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val progress = if (elapsed < progressDuration) {
                        (elapsed.toFloat() / progressDuration) * 0.9f
                    } else {
                        0.9f
                    }
                    _state.update { it.copy(loadingProgress = progress) }
                    if (progress >= 0.9f) break
                    delay(100) // Update every 100ms
                }
            }

            // Wait for quiz generation
            val result = generateQuizFromPdfUseCase(uri)

            // Cancel the progress animation job if still running
            progressJob.cancel()

            when (result) {
                is com.fcorallini.recall.core.data.common.Result.Success -> {
                    // Complete to 100% and wait 500ms for user to see
                    _state.update { it.copy(loadingProgress = 1f) }
                    delay(500)

                    _state.update {
                        it.copy(isLoading = false, loadingProgress = 0f, navigateToQuizId = result.data)
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loadingProgress = 0f,
                            errorMessage = result.exception.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}