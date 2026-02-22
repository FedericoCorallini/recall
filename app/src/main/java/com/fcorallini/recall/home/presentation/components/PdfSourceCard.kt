package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import kotlin.math.roundToInt

@Composable
fun PdfSourceCard(
    source: PdfSource,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onRenameClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0B1020).copy(alpha = 0.95f),
                            Color(0xFF1A2240).copy(alpha = 0.85f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = source.displayName.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename source",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable(onClick = onRenameClick)
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete source",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable(onClick = onDeleteClick)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        MainStatRow(
                            icon = Icons.Default.CheckCircle,
                            label = formatPracticeCount(source.practiceCount),
                            value = source.practiceCount.toString(),
                            highlight = false,
                            modifier = Modifier
                        )
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.End
                        ) {
                            StatRow(
                                icon = Icons.Default.Info,
                                label = "Last practice",
                                value = source.lastPracticedEpochMs?.let { formatRelativeTime(it) } ?: "Never",
                                highlight = false
                            )
                            Spacer(modifier = Modifier.width(32.dp))
                            StatRow(
                                icon = Icons.Default.DateRange,
                                label = "Created",
                                value = formatRelativeTime(source.createdAtEpochMs),
                                highlight = false
                            )
                        }
                    }
                    ScoreRing(
                        progress = source.averageScore.coerceIn(0f, 1f),
                        label = if (source.practiceCount > 0) "Avg score" else "No data",
                        modifier = Modifier.size(110.dp)
                    )
                }

                // Bottom button
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Practice Now")
                }
            }
        }
    }
}

@Composable
private fun ScoreRing(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    val percent = (progress * 100).roundToInt()
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    val progressColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Background ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 5.dp.toPx()
            val inset = strokeWidth / 2f
            val size = Size(size.width - strokeWidth, size.height - strokeWidth)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = size,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress ring
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = size,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (label == "No data") "—" else "$percent%",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MainStatRow(
    icon: ImageVector,
    label: String,
    value: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.sp
            ),
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

private fun formatRelativeTime(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - epochMs).coerceAtLeast(0)
    val minutes = diff / (60 * 1000)
    val hours = minutes / 6
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
        hours < 24 -> "$hours ${if (hours == 1L) "hour" else "hours"} ago"
        days < 7 -> "$days ${if (days == 1L) "day" else "days"} ago"
        else -> "${days / 7} ${if (days / 7 == 1L) "week" else "weeks"} ago"
    }
}
 
private fun formatPracticeCount(count: Int): String {
    return if (count == 1) "Practice" else "Practices"
}

@Preview(showBackground = true)
@Composable
private fun PdfSourceCardPreview() {
    RecallTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val sampleSource = PdfSource(
                id = "1",
                displayName = "Exploring Data Visually Long Text.pdf",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000,
                practiceCount = 8,
                lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                averageScore = 0.78f
            )

            PdfSourceCard(
                source = sampleSource,
                onClick = {},
                onDeleteClick = {},
                onRenameClick = {}
            )
        }
    }
}