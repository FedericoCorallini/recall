package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.presentation.theme.RecallTheme

@Composable
fun PdfSourcesList(
    pdfSources: List<PdfSource>,
    onSourceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📚 My Quizzes",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "${pdfSources.size} ${if (pdfSources.size == 1) "quiz" else "quizzes"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(pdfSources) { source ->
            PdfSourceCard(
                source = source,
                onClick = { onSourceClick(source.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bottom padding for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PdfSourcesListPreview() {
    RecallTheme {
        val sampleSources = listOf(
            PdfSource(
                id = "1",
                displayName = "Kotlin Coroutines Guide.pdf",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                practiceCount = 5,
                lastPracticedEpochMs = System.currentTimeMillis() - 60 * 60 * 1000,
                averageScore = 0.85f
            ),
            PdfSource(
                id = "2",
                displayName = "Clean Architecture Principles and Patterns.pdf",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                averageScore = 0.92f
            ),
            PdfSource(
                id = "3",
                displayName = "Android Development Best Practices.pdf",
                uriString = "content://sample/3",
                createdAtEpochMs = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000,
                practiceCount = 3,
                lastPracticedEpochMs = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                averageScore = 0.67f
            ),
            PdfSource(
                id = "4",
                displayName = "HTTP and REST APIs.pdf",
                uriString = "content://sample/4",
                createdAtEpochMs = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000,
                practiceCount = 0,
                lastPracticedEpochMs = null,
                averageScore = 0f
            )
        )

        PdfSourcesList(
            pdfSources = sampleSources,
            onSourceClick = {}
        )
    }
}
