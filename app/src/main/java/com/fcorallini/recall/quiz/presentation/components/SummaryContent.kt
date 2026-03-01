package com.fcorallini.recall.quiz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.quiz.presentation.QuizUiState

@Composable
fun SummaryContent(
    state: QuizUiState.Summary,
    onBackToHome: () -> Unit
) {
    val total = state.totalCount.coerceAtLeast(1)
    val correct = state.correctCount.coerceIn(0, total)
    val percentage = (correct.toFloat() / total.toFloat() * 100).toInt()

    val title = when {
        percentage >= 90 -> "Excellent work"
        percentage >= 70 -> "Great job"
        percentage >= 50 -> "Good progress"
        else -> "Keep practicing"
    }

    val subtitle = when {
        percentage >= 90 -> "You really know this material."
        percentage >= 70 -> "You’re getting solid results."
        percentage >= 50 -> "A few more sessions and you’ll nail it."
        else -> "Try another round to improve your score."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "$correct/$total",
                    letterSpacing = 4.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                // Score progress bar (similar to PracticeSessionsList)
                val scoreRatio = correct.toFloat() / total.toFloat()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    // Filled portion - green for correct
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(scoreRatio.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth().padding(14.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "Back to Home",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SummaryContentPreview() {
    RecallTheme {
        val state = QuizUiState.Summary(
            correctCount = 5,
            totalCount = 6
        )

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SummaryContent(
                    state = state,
                    onBackToHome = {}
                )
            }
        }
    }
}
