package com.fcorallini.recall.home.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.model.GlobalStats
import com.fcorallini.recall.home.presentation.components.EmptyHomeContent
import com.fcorallini.recall.home.presentation.components.GlobalStatsHeader
import com.fcorallini.recall.home.presentation.components.PdfSourcesList
import com.fcorallini.recall.core.presentation.theme.RecallTheme

@Composable
fun HomeScreen(
    onNavigateToQuiz: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(HomeEvent.GenerateFromPdf(it.toString()))
        }
    }

    // Handle navigation on success
    LaunchedEffect(state.navigateToQuizId) {
        state.navigateToQuizId?.let { quizId ->
            onNavigateToQuiz(quizId)
            viewModel.onEvent(HomeEvent.ResetState)
        }
    }

    // Handle error messages
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(HomeEvent.ResetState)
        }
    }

    HomeContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateToQuiz = onNavigateToQuiz,
        onUploadPdfClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
        onDeleteSource = { sourceId ->
            viewModel.onEvent(HomeEvent.DeletePdfSource(sourceId))
        },
        onRenameSource = { sourceId, newDisplayName ->
            viewModel.onEvent(
                HomeEvent.RenamePdfSource(
                    sourceId = sourceId,
                    newDisplayName = newDisplayName
                )
            )
        }
    )
}
 
@Composable
fun HomeContent(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    onNavigateToQuiz: (String) -> Unit,
    onUploadPdfClick: () -> Unit,
    onDeleteSource: (String) -> Unit,
    onRenameSource: (String, String) -> Unit
) {
    var renameTarget by remember { mutableStateOf<PdfSource?>(null) }
    var renameText by remember { mutableStateOf("") }
 
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.pdfSources.isNotEmpty() && !state.isLoading) {
                FloatingActionButton(
                    onClick = onUploadPdfClick,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 12.dp)
        ) {
            when {
                state.isLoading -> {
                    HomeLoadingContent()
                }
                state.pdfSources.isEmpty() -> {
                    EmptyHomeContent(onUploadPdfClick = onUploadPdfClick)
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        GlobalStatsHeader(
                            stats = state.globalStats,
                            quizzesCount = state.pdfSources.size,
                            modifier = Modifier.padding(24.dp)
                        )
                        PdfSourcesList(
                            pdfSources = state.pdfSources,
                            onSourceClick = onNavigateToQuiz,
                            onSourceDelete = { source -> onDeleteSource(source.id) },
                            onSourceRename = { source ->
                                renameTarget = source
                                renameText = source.displayName
                            }
                        )
                    }
                }
            }
        }
    }
 
    if (renameTarget != null) {
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("Rename PDF") },
            text = {
                TextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    placeholder = { Text("New name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = renameTarget
                        if (target != null && renameText.trim().isNotEmpty()) {
                            onRenameSource(target.id, renameText.trim())
                        }
                        renameTarget = null
                    },
                    enabled = renameText.trim().isNotEmpty()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HomeLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Analizando tu PDF...",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Estamos generando tus preguntas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(220.dp),
                strokeWidth = 10.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
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
                    .padding(paddingValues)
            ) {
                HomeLoadingContent()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeStatsPreview() {
    RecallTheme {
        HomeContent(
            state = HomeState(
                pdfSources = listOf(
                    PdfSource(
                        id = "1",
                        displayName = "Exploring Data Visually.pdf",
                        uriString = "content://sample/1",
                        createdAtEpochMs = System.currentTimeMillis(),
                        practiceCount = 5,
                        lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                        averageScore = 0.82f
                    ),
                    PdfSource(
                        id = "1",
                        displayName = "Exploring Data Visually.pdf",
                        uriString = "content://sample/1",
                        createdAtEpochMs = System.currentTimeMillis(),
                        practiceCount = 5,
                        lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                        averageScore = 0.82f
                    )
                ),
                globalStats = GlobalStats(
                    streakDays = 4,
                    totalPractices = 12,
                    averageScore = 0.76f,
                    lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToQuiz = {},
            onUploadPdfClick = {},
            onDeleteSource = {},
            onRenameSource = { _, _ -> }
        )
    }
}
