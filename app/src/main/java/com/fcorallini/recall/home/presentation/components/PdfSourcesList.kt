package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
    onSourceDelete: (PdfSource) -> Unit = {},
    onSourceRename: (PdfSource) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { pdfSources.size })

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📚 My Quizzes", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "${pdfSources.size} ${if (pdfSources.size == 1) "quiz" else "quizzes"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center)
        ) { page ->
            val source = pdfSources[page]
            PdfSourceCard(
                source = source,
                onClick = { onSourceClick(source.id) },
                onDeleteClick = { onSourceDelete(source) },
                onRenameClick = { onSourceRename(source) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
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
