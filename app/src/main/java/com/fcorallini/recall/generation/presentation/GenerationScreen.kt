package com.fcorallini.recall.generation.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.generation.presentation.components.LoadingBottomPanel
import com.fcorallini.recall.home.presentation.components.QuizPreviewBackground

@Composable
fun GenerationScreen(
    viewModel: GenerationViewModel = hiltViewModel(),
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    var pickerDismissed by rememberSaveable { mutableStateOf(false) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri != null) {
                    pickerDismissed = false
                    viewModel.onEvent(GenerationEvent.GenerateFromPdf(uri.toString()))
                } else {
                    pickerDismissed = true
                }
            }

    val launchPicker: () -> Unit = {
        pickerDismissed = false
        viewModel.onEvent(GenerationEvent.ResetState)
        pdfPickerLauncher.launch(arrayOf("application/pdf"))
    }

    LaunchedEffect(Unit) {
        pdfPickerLauncher.launch(arrayOf("application/pdf"))
    }

    LaunchedEffect(state.navigateToQuizId) {
        state.navigateToQuizId?.let { quizId ->
            onNavigateToQuiz(quizId)
            viewModel.onEvent(GenerationEvent.ResetState)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            state.isLoading -> {
                GenerationLoadingContent(
                    progress = state.loadingProgress,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            state.errorMessage != null -> {
                GenerationStatusContent(
                    title = "Something went wrong",
                    message = state.errorMessage,
                    primaryActionLabel = "Choose another PDF",
                    onPrimaryAction = launchPicker,
                    secondaryActionLabel = "Back to Home",
                    onSecondaryAction = onNavigateToHome,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            pickerDismissed -> {
                GenerationStatusContent(
                    title = "No PDF selected",
                    message = "Choose a PDF to start generating questions",
                    primaryActionLabel = "Choose PDF",
                    onPrimaryAction = launchPicker,
                    secondaryActionLabel = "Back to Home",
                    onSecondaryAction = onNavigateToHome,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                )
            }
}
    }
}

@Composable
fun GenerationLoadingContent(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = { it }),
        label = "progress"
    )

    Box(
        modifier = modifier
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
                    .fillMaxHeight(0.7f - animatedProgress)
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
private fun GenerationStatusContent(
    title: String,
    message: String,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    secondaryActionLabel: String,
    onSecondaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
                    .fillMaxHeight(0.7f)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.BottomCenter)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(vertical = 20.dp)
                .background(
                    color = androidx.compose.ui.graphics.Color(0xFF1E1D22).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = primaryActionLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = secondaryActionLabel,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Box(modifier = Modifier.height(8.dp))
        }
}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeLoadingContentPreview() {
    RecallTheme {
        GenerationLoadingContent(progress = 0.3f)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GenerationStatusContentPreview() {
    RecallTheme {
        GenerationStatusContent(
            title = "No PDF selected",
            message = "Choose a PDF to start generating questions.",
            primaryActionLabel = "Choose PDF",
            onPrimaryAction = {},
            secondaryActionLabel = "Back to Home",
            onSecondaryAction = {}
        )
    }
}