package com.fcorallini.recall.quiz.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.fcorallini.recall.core.domain.model.Question
import com.fcorallini.recall.core.domain.model.QuestionStats
import com.fcorallini.recall.core.domain.model.QuestionType
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.quiz.presentation.QuizUiState

@Composable
fun QuizContent(
    state: QuizUiState.Quiz,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Header with progress and close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${state.currentIndex + 1}/${state.totalQuestions}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.width(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close quiz",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1).toFloat() / state.totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Question prompt
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = state.currentQuestion.prompt,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Answer input based on question type
        when (state.currentQuestion.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                MultipleChoiceOptions(
                    options = state.currentQuestion.options,
                    selectedOption = state.userAnswer,
                    onOptionSelected = onAnswerChange,
                    isAnswerCorrect = state.isAnswerCorrect
                )
            }
            QuestionType.FLASHCARD -> {
                FlashcardInput(
                    answer = state.userAnswer,
                    onAnswerChange = onAnswerChange
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Submit button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = state.userAnswer.isNotBlank() && !state.isSubmitting,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(4.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Confirm", style = MaterialTheme.typography.titleMedium)
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
                    onSubmit = {},
                    onClose = {}
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
                    onSubmit = {},
                    onClose = {}
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
                    onSubmit = {},
                    onClose = {}
                )
            }
        }
    }
}
