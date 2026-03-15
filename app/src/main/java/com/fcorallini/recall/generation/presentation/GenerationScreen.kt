package com.fcorallini.recall.generation.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.core.presentation.theme.RecallTheme
import com.fcorallini.recall.generation.presentation.components.LoadingBottomPanel
import com.fcorallini.recall.home.presentation.components.QuizPreviewBackground

@Composable
fun GenerationScreen(
    viewModel: GenerationViewModel = hiltViewModel(),
    onNavigateToQuiz: (String) -> Unit
) {
    val state = viewModel.state.collectAsState().value

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(GenerationEvent.GenerateFromPdf(it.toString()))
        }
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

    GenerationLoadingContent(state.loadingProgress)
}

@Composable
fun GenerationLoadingContent(
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeLoadingContentPreview() {
    RecallTheme {
        GenerationLoadingContent(progress = 0.3f)
    }
}