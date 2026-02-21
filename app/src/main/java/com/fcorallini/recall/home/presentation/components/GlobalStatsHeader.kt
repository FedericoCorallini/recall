package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    title: String = "Progreso",
    subtitle: String = "Sigue practicando para mantener tu racha"
) {
    val on = MaterialTheme.colorScheme.onSurface
    val labelColor = on.copy(alpha = 0.70f)
    val hintColor = on.copy(alpha = 0.55f)
    val valueColor = on.copy(alpha = 0.95f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 18.dp)
    ) {

        // Header (círculo + título)
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
                    text = "Resumen de tu actividad",
                    style = MaterialTheme.typography.labelMedium,
                    color = hintColor
                )
            }
        }

        Spacer(Modifier.size(18.dp))

        // Primera fila: Racha (izq) y Eficacia (der)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Izquierda
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Racha activa",
                    style = MaterialTheme.typography.labelLarge,
                    color = hintColor
                )

                Spacer(Modifier.size(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (stats.streakDays == 0) "—" else stats.streakDays.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        color = valueColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.alignByBaseline()
                    )
                    Text(
                        text = " días",
                        style = MaterialTheme.typography.titleLarge,
                        color = labelColor,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .alignByBaseline()
                    )
                }
            }

            Spacer(Modifier.size(16.dp))

            // Derecha
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Eficacia promedio",
                    style = MaterialTheme.typography.labelLarge,
                    color = hintColor
                )

                Spacer(Modifier.size(8.dp))

                Text(
                    text = formatScore(stats.averageScore),
                    style = MaterialTheme.typography.displayMedium,
                    color = valueColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.size(22.dp))

        // Segunda fila: 3 stats (texto simple, alineado y con buen ritmo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            MiniStatText(
                value = stats.totalPractices.toString(),
                label = "Prácticas",
                valueColor = valueColor,
                labelColor = hintColor,
                align = Alignment.Start,
                modifier = Modifier.weight(1f)
            )
            MiniStatText(
                value = quizzesCount.toString(),
                label = "Quizzes",
                valueColor = valueColor,
                labelColor = hintColor,
                align = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            )
            MiniStatText(
                value = stats.lastPracticedEpochMs?.let { formatShortRelativeTime(it) } ?: "Nunca",
                label = "Última vez",
                valueColor = valueColor,
                labelColor = hintColor,
                align = Alignment.End,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.size(14.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = hintColor
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
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = labelColor
        )
    }
}

private fun formatScore(score: Float): String {
    if (score <= 0f) return "—"
    val percent = (score * 100).roundToInt()
    return "$percent%"
}

private fun formatShortRelativeTime(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - epochMs).coerceAtLeast(0)
    val minutes = diff / (60 * 1000)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Recién"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> "${days / 7} sem"
    }
}

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
                    lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000
                ),
                quizzesCount = 6
            )
        }
    }
}