package com.fcorallini.recall.list.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fcorallini.recall.R
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.model.PracticeSession
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.list.presentation.components.PdfSourcesList
import com.fcorallini.recall.list.presentation.components.PracticeSessionsList

@Composable
fun ListScreen(
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle error messages
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ListEvent.ResetError)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ListEvent.SelectSource(null))
    }

    ListContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateToQuiz = onNavigateToQuiz,
        onNavigateToHome = onNavigateToHome,
        onSelectSource = { sourceId ->
            viewModel.onEvent(ListEvent.SelectSource(sourceId))
        },
        onDeleteSource = { sourceId ->
            viewModel.onEvent(ListEvent.DeletePdfSource(sourceId))
        },
        onRenameSource = { sourceId, newDisplayName ->
            viewModel.onEvent(
                ListEvent.RenamePdfSource(
                    sourceId = sourceId,
                    newDisplayName = newDisplayName
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    state: ListState,
    snackbarHostState: SnackbarHostState,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onSelectSource: (String?) -> Unit,
    onDeleteSource: (String) -> Unit,
    onRenameSource: (String, String) -> Unit
) {
    var renameTarget by remember { mutableStateOf<PdfSource?>(null) }
    var renameText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFF242424),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Add PDF from List - not primary action here */ },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add PDF") },
                    label = { Text("Add PDF") },
                    enabled = false
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on List */ },
                    icon = { Icon(Icons.Default.List, contentDescription = "Quizzes") },
                    label = { Text("Quizzes") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with title and count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp)
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Quizzes",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${state.pdfSources.size} quizzes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                
                // Sources list with selection
                PdfSourcesList(
                    pdfSources = state.pdfSources,
                    selectedSourceId = state.selectedSourceId,
                    onSelectSource = onSelectSource,
                    onStartPractice = { sourceId -> onNavigateToQuiz(sourceId) },
                    onSourceDelete = { source -> onDeleteSource(source.id) },
                    onSourceRename = { source ->
                        renameTarget = source
                        renameText = source.displayName
                    },
                    modifier = Modifier.weight(1f),
                    practiceSessions = state.practiceSessions
                )

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

@Preview(showBackground = true)
@Composable
private fun ListContentPreview() {
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
                displayName = "Clean Architecture Principles.pdf",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                averageScore = 0.92f
            ),
            PdfSource(
                id = "3",
                displayName = "Kotlin Coroutines Guide.pdf",
                uriString = "content://sample/1",
                createdAtEpochMs = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                practiceCount = 5,
                lastPracticedEpochMs = System.currentTimeMillis() - 60 * 60 * 1000,
                averageScore = 0.85f
            ),
            PdfSource(
                id = "4",
                displayName = "Clean Architecture Principles.pdf",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                averageScore = 0.92f
            ),
            PdfSource(
                id = "5",
                displayName = "Clean Architecture Principles.pdf",
                uriString = "content://sample/2",
                createdAtEpochMs = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                practiceCount = 12,
                lastPracticedEpochMs = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                averageScore = 0.92f
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
            ),
            PracticeSession(
                id = 3,
                sourceId = "2",
                completedAtEpochMs = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                score = 0.45f,
                correctCount = 9,
                totalCount = 20
            ),
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
            ),
            PracticeSession(
                id = 3,
                sourceId = "2",
                completedAtEpochMs = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                score = 0.45f,
                correctCount = 9,
                totalCount = 20
            )
        )

        ListContent(
            state = ListState(pdfSources = sampleSources, selectedSourceId = "2", practiceSessions = listOf()),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToQuiz = {},
            onNavigateToHome = {},
            onSelectSource = {},
            onDeleteSource = {},
            onRenameSource = { _, _ -> }
        )
    }
}
