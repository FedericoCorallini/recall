package com.fcorallini.recall.home.presentation

import android.media.Image
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fcorallini.recall.R
import com.fcorallini.recall.core.domain.model.PdfSource
import com.fcorallini.recall.core.domain.model.GlobalStats
import com.fcorallini.recall.home.presentation.components.CurvedNavigationBarWithFab
import com.fcorallini.recall.home.presentation.components.EmptyHomeContent
import com.fcorallini.recall.home.presentation.components.GlobalStatsHeader
import com.fcorallini.recall.list.presentation.components.PdfSourceCard
import com.fcorallini.recall.core.presentation.theme.RecallTheme

@Composable
fun HomeScreen(
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToList: () -> Unit,
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
        onNavigateToList = onNavigateToList,
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
    onNavigateToList: () -> Unit,
    onUploadPdfClick: () -> Unit,
    onDeleteSource: (String) -> Unit,
    onRenameSource: (String, String) -> Unit
) {
    var renameTarget by remember { mutableStateOf<PdfSource?>(null) }
    var renameText by remember { mutableStateOf("") }
 
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onUploadPdfClick,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add PDF") },
                    label = { Text("Add") },
                    enabled = state.pdfSources.isNotEmpty() && !state.isLoading
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToList,
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
            when {
                state.isLoading -> {
                    HomeLoadingContent()
                }
                state.pdfSources.isEmpty() -> {
                    EmptyHomeContent(onUploadPdfClick = onUploadPdfClick)
                }
                else -> {
                    HomeMainContent(
                        state = state,
                        onNavigateToQuiz = onNavigateToQuiz,
                        onDeleteSource = onDeleteSource,
                        onRenameSource = { source ->
                            renameTarget = source
                            renameText = source.displayName
                        }
                    )
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
 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeMainContent(
    state: HomeState,
    onNavigateToQuiz: (String) -> Unit,
    onDeleteSource: (String) -> Unit,
    onRenameSource: (PdfSource) -> Unit
) {
    // Get the most recent PDF source (first in the list, assuming sorted by createdAt)
    val mostRecentSource = state.pdfSources.firstOrNull()

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF242424))
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GlobalStatsHeader(
                stats = state.globalStats
            )

            Text(
                text = "Recommended for today",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 70.dp),
                textAlign = TextAlign.Center
            )

            mostRecentSource?.let { source ->
                PdfSourceCard(
                    source = source,
                    onCardClick = { onNavigateToQuiz(source.id) },
                    onDeleteClick = { onDeleteSource(source.id) },
                    onRenameClick = { onRenameSource(source) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Keep your streak alive",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Active recall builds long-term memory",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
                text = "Analyzing your PDF...",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We are generating your questions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
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



@Preview(showBackground = true)
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
                        id = "2",
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
            onNavigateToList = {},
            onUploadPdfClick = {},
            onDeleteSource = {},
            onRenameSource = { _, _ -> }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeEmptyPreview() {
    RecallTheme {
        HomeContent(
            state = HomeState(
                pdfSources = emptyList(),
                globalStats = GlobalStats(
                    streakDays = 4,
                    totalPractices = 12,
                    averageScore = 0.76f,
                    lastPracticedEpochMs = System.currentTimeMillis() - 2 * 60 * 60 * 1000
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToQuiz = {},
            onNavigateToList = {},
            onUploadPdfClick = {},
            onDeleteSource = {},
            onRenameSource = { _, _ -> }
        )
    }
}
