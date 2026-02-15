package com.fcorallini.recall.quiz.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FlashcardInput(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    OutlinedTextField(
        value = answer,
        onValueChange = onAnswerChange,
        label = { Text("Your answer") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 5
    )
}
