package com.fcorallini.recall.list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fcorallini.recall.core.domain.model.PracticeSession
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PracticeSessionsList(
    sessions: List<PracticeSession>,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Practice History",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (sessions.isEmpty()) {
            Text(
                text = "No practice sessions yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sessions.forEach {
                    PracticeSessionRow(
                        session = it,
                        dateFormat = dateFormat
                    )
                }

            }
        }
    }
}

@Composable
private fun PracticeSessionRow(
    session: PracticeSession,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    val scorePercent = (session.score * 100).toInt()
    val barColor = when {
        session.score >= 0.8f -> Color(0xFF4CAF50) // Green
        session.score >= 0.6f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFE91E63) // Red
    }
    val backgroundBarColor = barColor.copy(alpha = 0.2f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date on the left
        Text(
            text = dateFormat.format(Date(session.completedAtEpochMs)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.width(100.dp)
        )

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundBarColor)
        ) {
            // Filled portion
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(session.score.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(barColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PracticeSessionsListPreview() {
    RecallTheme {
        val sampleSessions = listOf(
            PracticeSession(
                id = 1,
                sourceId = "1",
                completedAtEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                score = 0.85f,
                correctCount = 17,
                totalCount = 20
            ),
            PracticeSession(
                id = 2,
                sourceId = "1",
                completedAtEpochMs = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                score = 0.72f,
                correctCount = 18,
                totalCount = 25
            ),
            PracticeSession(
                id = 3,
                sourceId = "1",
                completedAtEpochMs = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                score = 0.45f,
                correctCount = 9,
                totalCount = 20
            )
        )

        PracticeSessionsList(
            sessions = sampleSessions,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
