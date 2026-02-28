package com.fcorallini.recall.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.list.domain.usecase.GetPdfSourcesUseCase
import com.fcorallini.recall.list.domain.usecase.DeletePdfSourceUseCase
import com.fcorallini.recall.list.domain.usecase.RenamePdfSourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getPdfSourcesUseCase: GetPdfSourcesUseCase,
    private val deletePdfSourceUseCase: DeletePdfSourceUseCase,
    private val renamePdfSourceUseCase: RenamePdfSourceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    init {
        observePdfSources()
    }

    private fun observePdfSources() {
        viewModelScope.launch {
            getPdfSourcesUseCase().collect { sources ->
                _state.update { it.copy(pdfSources = sources) }
            }
        }
    }

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.DeletePdfSource -> deletePdfSource(event.sourceId)
            is ListEvent.RenamePdfSource -> renamePdfSource(
                sourceId = event.sourceId,
                newDisplayName = event.newDisplayName
            )
            is ListEvent.ResetError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun deletePdfSource(sourceId: String) {
        viewModelScope.launch {
            when (val result = deletePdfSourceUseCase(sourceId)) {
                is Result.Success -> Unit
                is Result.Error -> _state.update {
                    it.copy(errorMessage = result.exception.message ?: "Failed to delete source")
                }
            }
        }
    }

    private fun renamePdfSource(sourceId: String, newDisplayName: String) {
        if (newDisplayName.isBlank()) {
            _state.update { it.copy(errorMessage = "Name cannot be empty") }
            return
        }
        viewModelScope.launch {
            when (val result = renamePdfSourceUseCase(sourceId, newDisplayName.trim())) {
                is Result.Success -> Unit
                is Result.Error -> _state.update {
                    it.copy(errorMessage = result.exception.message ?: "Failed to rename source")
                }
            }
        }
    }
}
