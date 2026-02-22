package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.fcorallini.recall.core.domain.model.GlobalStats
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import kotlin.math.roundToInt

@Composable
fun GlobalStatsHeader(
    stats: GlobalStats,
    quizzesCount: Int,
    modifier: Modifier = Modifier,
    title: String = "Progress",
    subtitle: String = "Keep practicing to maintain your streak"
) {
    val on = MaterialTheme.colorScheme.onSurface
    val labelColor = on.copy(alpha = 0.70f)
    val hintColor = on.copy(alpha = 0.55f)
    val valueColor = on.copy(alpha = 0.95f)

    Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column {

        // Header (circle + title)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(on.copy(alpha = 0.10f))
                    .border(1.dp, on.copy(alpha = 0.10f), CircleShape)
            )
            Column(Modifier.padding(start = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = valueColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Activity summary",
                    style = MaterialTheme.typography.labelMedium,
                    color = hintColor
                )
            }
            MiniStatText(
                value = quizzesCount.toString(),
                label = "Quizzes",
                valueColor = valueColor,
                labelColor = hintColor,
                align = Alignment.End,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.size(18.dp))

        // First row: streak, last time, effectiveness
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Last time",
                    style = MaterialTheme.typography.labelLarge,
                    color = hintColor
                )

                Spacer(Modifier.size(4.dp))
                val lastTime = stats.lastPracticedEpochMs?.let { formatShortRelativeTime(it) }
                StatValueWithSuffix(
                    value = lastTime?.value ?: "—",
                    suffix = lastTime?.suffix,
                    valueColor = valueColor,
                    suffixColor = labelColor
                )
            }


            Spacer(Modifier.size(16.dp))

            // Center
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Effectiveness",
                    style = MaterialTheme.typography.labelLarge,
                    color = hintColor
                )

                Spacer(Modifier.size(4.dp))
                StatValueWithSuffix(
                    value = formatScoreValue(stats.averageScore),
                    suffix = formatScoreSuffix(stats.averageScore),
                    valueColor = valueColor,
                    suffixColor = labelColor
                )
            }

            Spacer(Modifier.size(16.dp))

            // End
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    text = "Active streak",
                    style = MaterialTheme.typography.labelLarge,
                    color = hintColor
                )

                Spacer(Modifier.size(4.dp))

                StatValueWithSuffix(
                    value = if (stats.streakDays == 0) "0" else stats.streakDays.toString(),
                    suffix = "days",
                    valueColor = valueColor,
                    suffixColor = labelColor
                )
            }
        }

        Spacer(Modifier.size(14.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = hintColor
        )
            }
        }
}

@Composable
private fun MiniStatText(
    value: String,
    label: String,
    valueColor: Color,
    labelColor: Color,
    align: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = align
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = labelColor
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatScoreValue(score: Float): String {
    if (score <= 0f) return "—"
    val percent = (score * 100).roundToInt()
    return percent.toString()
}
 
private fun formatScoreSuffix(score: Float): String? {
    return if (score <= 0f) null else "%"
}
 
private data class ShortRelativeTime(val value: String, val suffix: String?)
 
private fun formatShortRelativeTime(epochMs: Long): ShortRelativeTime {
    val now = System.currentTimeMillis()
    val diff = (now - epochMs).coerceAtLeast(0)
    val minutes = diff / (60 * 1000)
    val hours = minutes / 60
    val days = hours / 24
 
    return when {
        minutes < 1 -> ShortRelativeTime("0", "m ago")
        minutes < 60 -> ShortRelativeTime(minutes.toString(), "m ago")
        hours < 24 -> ShortRelativeTime(hours.toString(), "h ago")
        days < 7 -> ShortRelativeTime(days.toString(), "d ago")
        else -> ShortRelativeTime((days / 7).toString(), "w ago")
    }
}
 
@Composable
private fun StatValueWithSuffix(
    value: String,
    suffix: String?,
    valueColor: Color,
    suffixColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium,
            color = valueColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alignByBaseline()
        )
        if (!suffix.isNullOrBlank()) {
            Text(
                text = suffix,
                style = MaterialTheme.typography.titleMedium,
                color = suffixColor,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .alignByBaseline()
            )
        }
    }
}

// formatShortRelativeTime moved above to return value/suffix

@Preview(showBackground = true)
@Composable
private fun GlobalStatsHeaderPreview() {
    RecallTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GlobalStatsHeader(
                stats = GlobalStats(
                    streakDays = 2,
                    totalPractices = 1,
                    averageScore = 0.76f,
                    lastPracticedEpochMs = System.currentTimeMillis() - 0 * 60 * 60 * 1000
                ),
                quizzesCount = 6
            )
        }
    }
}