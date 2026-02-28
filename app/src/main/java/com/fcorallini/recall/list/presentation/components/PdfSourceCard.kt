package com.fcorallini.recall.list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.presentation.theme.RecallTheme

enum class ActionButton {
    EDIT,
    DELETE,
    NONE
}

@Composable
fun PdfSourceCard(
    source: PdfSource,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onRenameClick: () -> Unit = {},
    actionButton: ActionButton = ActionButton.EDIT,
    cardColor: Color = Color(0xFF5B8AD8),
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(28.dp)

    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth().height(192.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 44.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: Title and action icons
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = source.displayName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                when (actionButton) {
                    ActionButton.EDIT -> {
                        IconButton(onClick = onRenameClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rename source",
                                tint = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    ActionButton.DELETE -> {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete source",
                                tint = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    ActionButton.NONE -> {}
                }
            }

            // Divider line
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.15f),
                thickness = 1.dp
            )

            // Bottom row: 3 stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Practice Count
                StatItem(
                    value = source.practiceCount.toString(),
                    label = if (source.practiceCount == 1) "Practice" else "Practices"
                )

                // Average Score
                val scorePercent = (source.averageScore * 100).toInt()
                StatItem(
                    value = if (source.practiceCount > 0) "$scorePercent%" else "—",
                    label = "Avg Score"
                )

                // Last Practiced
                StatItem(
                    value = source.lastPracticedEpochMs?.let { formatRelativeTimeShort(it) } ?: "Never",
                    label = "Last"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp
            ),
            color = Color.Black.copy(alpha = 0.6f)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            ),
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}

private fun formatRelativeTimeShort(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - epochMs).coerceAtLeast(0)
    val minutes = diff / (60 * 1000)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> "${days / 7}w"
    }
}

@Preview(showBackground = true)
@Composable
private fun PdfSourceCardPreview() {
    RecallTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val sampleSource = PdfSource(
                id = "1",
                displayName = "The Crossword",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000,
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                averageScore = 0.85f
            )

            PdfSourceCard(
                source = sampleSource,
                onCardClick = {},
                onDeleteClick = {},
                onRenameClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PdfSourceCardEmptyPreview() {
    RecallTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val sampleSource = PdfSource(
                id = "2",
                displayName = "Kotlin Coroutines Guide",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                practiceCount = 0,
                lastPracticedEpochMs = null,
                averageScore = 0f
            )

            PdfSourceCard(
                source = sampleSource,
                onCardClick = {},
                onDeleteClick = {},
                onRenameClick = {}
            )
        }
    }
}
