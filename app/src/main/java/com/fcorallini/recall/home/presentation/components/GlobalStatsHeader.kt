package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.R
import com.fcorallini.recall.core.domain.model.GlobalStats
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import kotlin.math.roundToInt

@Composable
fun GlobalStatsHeader(
    stats: GlobalStats
) {
    val on = MaterialTheme.colorScheme.onSurface
    val labelColor = on.copy(alpha = 0.90f)
    val hintColor = on.copy(alpha = 0.55f)
    val valueColor = on.copy(alpha = 0.95f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(75.dp).padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Recall",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Build smarter quizzes with AI",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "Active streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor
                )

                Spacer(Modifier.size(4.dp))

                StatValueWithSuffix(
                    value = if (stats.streakDays == 0) "0" else stats.streakDays.toString(),
                    suffix = "days",
                    valueColor = valueColor,
                    suffixColor = labelColor
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Effectiveness",
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor
                )

                Spacer(Modifier.size(4.dp))
                StatValueWithSuffix(
                    value = formatScoreValue(stats.averageScore),
                    suffix = formatScoreSuffix(stats.averageScore),
                    valueColor = valueColor,
                    suffixColor = labelColor
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 75.dp)
                .background(hintColor)
        )

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
            style = MaterialTheme.typography.displaySmall,
            color = valueColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alignByBaseline()
        )
        if (!suffix.isNullOrBlank()) {
            Text(
                text = suffix,
                style = MaterialTheme.typography.labelMedium,
                color = suffixColor,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline()
            )
        }
    }
}

// formatShortRelativeTime moved above to return value/suffix

@Preview(showBackground = false)
@Composable
private fun GlobalStatsHeaderPreview() {
    RecallTheme {
        Box() {
            GlobalStatsHeader(
                stats = GlobalStats(
                    streakDays = 2,
                    totalPractices = 1,
                    averageScore = 0.76f,
                    lastPracticedEpochMs = System.currentTimeMillis() - 0 * 60 * 60 * 1000
                )
            )
        }
    }
}