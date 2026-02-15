package com.fcorallini.recall.quiz.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.core.domain.model.Question
import com.fcorallini.recall.core.domain.model.QuestionStats
import com.fcorallini.recall.core.domain.model.QuestionType
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.quiz.presentation.QuizUiState

@Composable
fun QuizContent(
    state: QuizUiState.Quiz,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Progress indicator
        Column {
            Text(
                text = "Question ${state.currentIndex + 1} of ${state.totalQuestions}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / state.totalQuestions.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Question prompt
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = state.currentQuestion.prompt,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer input based on question type
        when (state.currentQuestion.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                MultipleChoiceOptions(
                    options = state.currentQuestion.options,
                    selectedOption = state.userAnswer,
                    onOptionSelected = onAnswerChange
                )
            }
            QuestionType.FLASHCARD -> {
                FlashcardInput(
                    answer = state.userAnswer,
                    onAnswerChange = onAnswerChange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.userAnswer.isNotBlank() && !state.isSubmitting
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(4.dp)
                )
            } else {
                Text("Confirm")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizContentMultipleChoicePreview() {
    RecallTheme {
        val sampleQuestion = Question(
            id = "1",
            sourceId = "source1",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Which of the following is the correct way to launch a coroutine in Kotlin?",
            options = listOf(
                "launch { }",
                "async { }",
                "runBlocking { }",
                "All of the above"
            ),
            answer = "All of the above",
            stats = QuestionStats()
        )

        val state = QuizUiState.Quiz(
            currentQuestion = sampleQuestion,
            currentIndex = 0,
            totalQuestions = 6,
            userAnswer = "launch { }",
            isSubmitting = false
        )

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                QuizContent(
                    state = state,
                    onAnswerChange = {},
                    onSubmit = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizContentFlashcardPreview() {
    RecallTheme {
        val sampleQuestion = Question(
            id = "2",
            sourceId = "source1",
            type = QuestionType.FLASHCARD,
            prompt = "What does SOLID stand for in software engineering principles?",
            options = emptyList(),
            answer = "Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion",
            stats = QuestionStats()
        )

        val state = QuizUiState.Quiz(
            currentQuestion = sampleQuestion,
            currentIndex = 4,
            totalQuestions = 6,
            userAnswer = "Single Responsibility, Open-Closed",
            isSubmitting = false
        )

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                QuizContent(
                    state = state,
                    onAnswerChange = {},
                    onSubmit = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizContentSubmittingPreview() {
    RecallTheme {
        val sampleQuestion = Question(
            id = "3",
            sourceId = "source1",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "What HTTP status code indicates a successful GET request?",
            options = listOf(
                "200 OK",
                "201 Created",
                "204 No Content",
                "404 Not Found"
            ),
            answer = "200 OK",
            stats = QuestionStats()
        )

        val state = QuizUiState.Quiz(
            currentQuestion = sampleQuestion,
            currentIndex = 1,
            totalQuestions = 6,
            userAnswer = "200 OK",
            isSubmitting = true
        )

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                QuizContent(
                    state = state,
                    onAnswerChange = {},
                    onSubmit = {}
                )
            }
        }
    }
}
