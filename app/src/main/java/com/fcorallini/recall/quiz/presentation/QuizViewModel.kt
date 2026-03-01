package com.fcorallini.recall.quiz.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.model.Question
import com.fcorallini.recall.quiz.domain.usecase.ObserveQuestionsBySourceUseCase
import com.fcorallini.recall.quiz.domain.usecase.SubmitAnswerUseCase
import com.fcorallini.recall.quiz.domain.usecase.UpdatePdfSourceStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class QuizUiState {
    data object Loading : QuizUiState()
    data class Quiz(
        val currentQuestion: Question,
        val currentIndex: Int,
        val totalQuestions: Int,
        val userAnswer: String = "",
        val isSubmitting: Boolean = false,
        val isAnswerCorrect: Boolean? = null
    ) : QuizUiState()
    data class Summary(
        val correctCount: Int,
        val totalCount: Int
    ) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeQuestionsUseCase: ObserveQuestionsBySourceUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val updatePdfSourceStatsUseCase: UpdatePdfSourceStatsUseCase
) : ViewModel() {

    private val sourceId: String = savedStateHandle["sourceId"] ?: ""

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            observeQuestionsUseCase(sourceId).collect { questionList ->
                if (questionList.isEmpty()) {
                    _uiState.value = QuizUiState.Error("No questions found for this source")
                } else if (_uiState.value is QuizUiState.Loading) {
                    questions = questionList
                    showCurrentQuestion()
                }
            }
        }
    }

    private fun showCurrentQuestion() {
        if (currentQuestionIndex < questions.size) {
            _uiState.value = QuizUiState.Quiz(
                currentQuestion = questions[currentQuestionIndex],
                currentIndex = currentQuestionIndex,
                totalQuestions = questions.size,
                userAnswer = "",
                isSubmitting = false,
                isAnswerCorrect = null
            )
        } else {
            // Quiz completed - update PDF source stats
            viewModelScope.launch {
                updatePdfSourceStatsUseCase(
                    sourceId = sourceId,
                    correctCount = correctAnswersCount,
                    totalCount = questions.size
                )
            }
            
            _uiState.value = QuizUiState.Summary(
                correctCount = correctAnswersCount,
                totalCount = questions.size
            )
        }
    }

    fun updateUserAnswer(answer: String) {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Quiz && !currentState.isSubmitting) {
            _uiState.value = currentState.copy(userAnswer = answer)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState !is QuizUiState.Quiz || currentState.userAnswer.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true)

            val result = submitAnswerUseCase(
                questionId = currentState.currentQuestion.id,
                userAnswer = currentState.userAnswer
            )

            when (result) {
                is Result.Success -> {
                    // Check if answer was correct
                    val isCorrect = currentState.userAnswer.trim().equals(
                        currentState.currentQuestion.answer.trim(),
                        ignoreCase = true
                    )
                    if (isCorrect) {
                        correctAnswersCount++
                        _uiState.value = currentState.copy(
                            isAnswerCorrect = true,
                            isSubmitting = true
                        )
                    }
                    else{
                        _uiState.value = currentState.copy(
                            isAnswerCorrect = false,
                            isSubmitting = true
                        )
                    }
                    delay(5000)
                    // Move to next question
                    currentQuestionIndex++
                    showCurrentQuestion()
                }
                is Result.Error -> {
                    _uiState.value = QuizUiState.Error(
                        result.exception.message ?: "Failed to submit answer"
                    )
                }
            }
        }
    }
}
