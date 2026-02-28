package com.fcorallini.recall.list.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fcorallini.recall.core.domain.model.PdfSource
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
    onSourceClick: (String) -> Unit,
    onSourceDelete: (PdfSource) -> Unit = {},
    onSourceRename: (PdfSource) -> Unit = {},
    modifier: Modifier = Modifier,
    overlap: Dp = 40.dp,
    cardHeight: Dp = 240.dp
) {
    var selectedId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val selectedIndex = selectedId?.let { id ->
        pdfSources.indexOfFirst { it.id == id }
    } ?: -1
    val hasSelection = selectedIndex >= 0
    val step = cardHeight - overlap
 
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(0)
        }
    }
 
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(-overlap),
        modifier = modifier.fillMaxWidth().background(Color(0xFF242424))
    ) {
        itemsIndexed(
            items = pdfSources,
            key = { _, source -> source.id }
        ) { index, source ->
            val isSelected = source.id == selectedId
            val targetOffset = when {
                !hasSelection -> 0.dp
                isSelected -> (-step * selectedIndex)
                else -> 0.dp
            }
            val animatedOffset by animateDpAsState(targetValue = targetOffset, label = "cardOffset")
            val targetAlpha = if (hasSelection && !isSelected) 0f else 1f
            val animatedAlpha by animateFloatAsState(targetValue = targetAlpha, label = "cardAlpha")
            PdfSourceCard(
                source = source,
                onCardClick = {
                    selectedId = if (isSelected) null else source.id
                },
                onDeleteClick = { onSourceDelete(source) },
                onRenameClick = { onSourceRename(source) },
                cardColor = cardColors[index % cardColors.size],
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = animatedOffset)
                    .alpha(animatedAlpha)
                    .zIndex(if (isSelected) 1f else 0f)
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
