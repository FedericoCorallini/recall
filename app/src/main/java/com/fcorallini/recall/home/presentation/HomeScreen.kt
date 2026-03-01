package com.fcorallini.recall.home.presentation

import android.media.Image
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.fcorallini.recall.home.presentation.components.QuizPreviewBackground
import com.fcorallini.recall.list.presentation.components.PdfSourceCard
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.home.presentation.components.GlobalStatsHeader

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

    when {
        state.isLoading -> {
            HomeLoadingContent(progress = state.loadingProgress)
        }
        state.pdfSources.isEmpty() -> {
            EmptyHomeContent(onUploadPdfClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) })
        }
        else -> {
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
    }
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
        containerColor = MaterialTheme.colorScheme.background,
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
                    label = { Text("Add PDF") },
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2022))
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
                    onStartPractice = { onNavigateToQuiz(source.id) },
                    onDeleteClick = { onDeleteSource(source.id) },
                    onRenameClick = { onRenameSource(source) },
                    isHomeCard = true,
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
private fun HomeLoadingContent(
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = { it }),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 26.dp)
        ) {
            QuizPreviewBackground()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f - animatedProgress)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.BottomCenter)
            )
        }

        LoadingBottomPanel(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun LoadingBottomPanel(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF1E1D22), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .padding(vertical = 22.dp)
            .height(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Headline
        Text(
            text = "We are creating your quiz",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(Modifier.height(12.dp))

        // Progress percentage text
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "This could take a few seconds",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
        )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeLoadingContentPreview() {
    RecallTheme {
        HomeLoadingContent(progress = 0.6f)
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
