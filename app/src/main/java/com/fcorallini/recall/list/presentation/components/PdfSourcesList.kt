package com.fcorallini.recall.list.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.model.PracticeSession
import com.fcorallini.recall.core.presentation.theme.RecallTheme

val cardColors = listOf(
    Color(0xFF5B8AD8),
    Color(0xFFD3D6DB),
    Color(0xFF9B7ED8),
    Color(0xFFD9BF25),
    Color(0xFF279696),
    Color(0xFFD65A5A),
    Color(0xFF55AD59),
)

@Composable
fun PdfSourcesList(
    pdfSources: List<PdfSource>,
    selectedSourceId: String?,
    practiceSessions: List<PracticeSession>,
    onSelectSource: (String?) -> Unit,
    onStartPractice: (String) -> Unit,
    onSourceDelete: (PdfSource) -> Unit = {},
    onSourceRename: (PdfSource) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val hasSelection = selectedSourceId != null

    // Cuando hay selección, solo mostrar ese item. Si no, mostrar todos.
    val itemsToShow = if (hasSelection) {
        pdfSources.filter { it.id == selectedSourceId }
    } else {
        pdfSources
    }

    val selectedIndex = if (hasSelection) {
        pdfSources.indexOfFirst { it.id == selectedSourceId }
    } else -1

    LaunchedEffect(selectedSourceId) {
            listState.animateScrollToItem(0)

    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(if (hasSelection) 0.dp else (-40).dp),
        modifier = modifier.fillMaxWidth().background(Color(0xFF242424))
    ) {
        itemsIndexed(
            items = itemsToShow,
            key = { _, source -> source.id }
        ) { index, source ->
            val isSelected = source.id == selectedSourceId
            val originalIndex = pdfSources.indexOfFirst { it.id == source.id }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (isSelected) 1f else 0f)
                    .padding(bottom = if (isSelected) 16.dp else 0.dp)
            ) {
                PdfSourceCard(
                    source = source,
                    onCardClick = {
                        onSelectSource(if (isSelected) null else source.id)
                    },
                    onDeleteClick = { onSourceDelete(source) },
                    onRenameClick = { onSourceRename(source) },
                    onStartPractice = { onStartPractice(source.id) },
                    isHomeCard = isSelected,
                    actionButton = ActionButton.EDIT,
                    cardColor = cardColors[originalIndex % cardColors.size],
                    modifier = Modifier.fillMaxWidth()
                )

                // Practice sessions debajo de la card seleccionada
                if (isSelected && practiceSessions.isNotEmpty()) {
                    PracticeSessionsList(
                        sessions = practiceSessions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .heightIn(max = 250.dp)
                    )
                }
            }
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

        val sampleSessions = listOf(
            PracticeSession(
                id = 1,
                sourceId = "2",
                completedAtEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                score = 0.85f,
                correctCount = 17,
                totalCount = 20
            ),
            PracticeSession(
                id = 2,
                sourceId = "2",
                completedAtEpochMs = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                score = 0.72f,
                correctCount = 18,
                totalCount = 25
            )
        )

        PdfSourcesList(
            pdfSources = sampleSources,
            selectedSourceId = "2",
            practiceSessions = sampleSessions,
            onSelectSource = {},
            onStartPractice = {}
        )
    }
}
