package com.fcorallini.recall.home.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fcorallini.recall.ui.theme.RecallTheme

@Composable
fun HomeScreen(
    onNavigateToQuiz: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pdfSources by viewModel.pdfSources.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.generateFromPdf(it.toString())
        }
    }

    // Handle navigation on success
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is HomeUiState.Success -> {
                onNavigateToQuiz(state.sourceId)
                viewModel.resetState()
            }
            is HomeUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (pdfSources.isNotEmpty() && uiState !is HomeUiState.Loading) {
                ExtendedFloatingActionButton(
                    onClick = {
                        pdfPickerLauncher.launch(arrayOf("application/pdf"))
                    },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = null)
                    },
                    text = {
                        Text("Upload PDF")
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Generating quiz questions...")
                        }
                    }
                }
                else -> {
                    if (pdfSources.isEmpty()) {
                        EmptyHomeContent(
                            onUploadPdfClick = {
                                pdfPickerLauncher.launch(arrayOf("application/pdf"))
                            }
                        )
                    } else {
                        PdfSourcesList(
                            pdfSources = pdfSources,
                            onSourceClick = onNavigateToQuiz
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHomeContent(
    onUploadPdfClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📚 Recall",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Transform your PDFs into interactive quizzes and flashcards",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onUploadPdfClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload PDF")
        }
    }
}

@Composable
private fun PdfSourcesList(
    pdfSources: List<com.fcorallini.recall.core.model.PdfSource>,
    onSourceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
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
        
        // Add some bottom padding for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PdfSourceCard(
    source: com.fcorallini.recall.core.model.PdfSource,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = source.displayName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Practice count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${source.practiceCount} ${if (source.practiceCount == 1) "practice" else "practices"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Average score
                if (source.practiceCount > 0) {
                    Text(
                        text = "Avg: ${(source.averageScore * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Last practiced
            if (source.lastPracticedEpochMs != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last practiced: ${formatRelativeTime(source.lastPracticedEpochMs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatRelativeTime(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMs
    val minutes = diff / (60 * 1000)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
        hours < 24 -> "$hours ${if (hours == 1L) "hour" else "hours"} ago"
        days < 7 -> "$days ${if (days == 1L) "day" else "days"} ago"
        else -> "${days / 7} ${if (days / 7 == 1L) "week" else "weeks"} ago"
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EmptyHomePreview() {
    RecallTheme {
        EmptyHomeContent(
            onUploadPdfClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PdfSourcesListPreview() {
    RecallTheme {
        val sampleSources = listOf(
            com.fcorallini.recall.core.model.PdfSource(
                id = "1",
                displayName = "Kotlin Coroutines Guide.pdf",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
                practiceCount = 5,
                lastPracticedEpochMs = System.currentTimeMillis() - 60 * 60 * 1000, // 1 hour ago
                averageScore = 0.85f
            ),
            com.fcorallini.recall.core.model.PdfSource(
                id = "2",
                displayName = "Clean Architecture Principles and Patterns.pdf",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000, // 1 week ago
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 3 * 60 * 60 * 1000, // 3 hours ago
                averageScore = 0.92f
            ),
            com.fcorallini.recall.core.model.PdfSource(
                id = "3",
                displayName = "Android Development Best Practices.pdf",
                uriString = "content://sample/3",
                createdAtEpochMs = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000, // 2 weeks ago
                practiceCount = 3,
                lastPracticedEpochMs = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000, // 5 days ago
                averageScore = 0.67f
            ),
            com.fcorallini.recall.core.model.PdfSource(
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

@Preview(showBackground = true)
@Composable
private fun PdfSourceCardPreview() {
    RecallTheme {
        PdfSourceCard(
            source = com.fcorallini.recall.core.model.PdfSource(
                id = "1",
                displayName = "Kotlin Coroutines Guide.pdf",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis(),
                practiceCount = 5,
                lastPracticedEpochMs = System.currentTimeMillis() - 60 * 60 * 1000,
                averageScore = 0.85f
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeLoadingPreview() {
    RecallTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Generating quiz questions...")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home - Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PdfSourcesListDarkPreview() {
    RecallTheme {
        val sampleSources = listOf(
            com.fcorallini.recall.core.model.PdfSource(
                id = "1",
                displayName = "Kotlin Coroutines Guide.pdf",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis(),
                practiceCount = 5,
                lastPracticedEpochMs = System.currentTimeMillis() - 60 * 60 * 1000,
                averageScore = 0.85f
            ),
            com.fcorallini.recall.core.model.PdfSource(
                id = "2",
                displayName = "Clean Architecture.pdf",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis(),
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                averageScore = 0.92f
            )
        )
        
        PdfSourcesList(
            pdfSources = sampleSources,
            onSourceClick = {}
        )
    }
}
