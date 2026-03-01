package com.fcorallini.recall.quiz.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.core.presentation.theme.RecallTheme

@Composable
fun MultipleChoiceOptions(
    options: List<String>,
    selectedOption: String,
    isAnswerCorrect: Boolean? = null,
    onOptionSelected: (String) -> Unit,
    enable: Boolean = true
) {
    Column {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .selectable(
                        selected = isSelected,
                        onClick = { if (enable) onOptionSelected(option) },
                        role = Role.RadioButton,
                        enabled = enable
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSelected && isAnswerCorrect == true -> Color(0xFF55AD59).copy(alpha = 0.15f)
                        isSelected && isAnswerCorrect == false -> Color(0xFFD65A5A).copy(alpha = 0.15f)
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = when {
                        isSelected && isAnswerCorrect == true -> Color(0xFF55AD59).copy(alpha = 0.45f)
                        isSelected && isAnswerCorrect == false -> Color(0xFFD65A5A).copy(alpha = 0.45f)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = when {
                                isSelected && isAnswerCorrect == true -> Color(0xFF55AD59)
                                isSelected && isAnswerCorrect == false -> Color(0xFFD65A5A)
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.primary
                            },
                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceOptionsDefaultPreview() {
    val options = listOf("Opción A", "Opción B", "Opción C", "Opción D")
    RecallTheme {
        MultipleChoiceOptions(
            options = options,
            selectedOption = "",
            isAnswerCorrect = null,
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceOptionsCorrectPreview() {
    val options = listOf("Opción A", "Opción B", "Opción C", "Opción D")
    RecallTheme {
        MultipleChoiceOptions(
            options = options,
            selectedOption = "Opción B",
            isAnswerCorrect = true,
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceOptionsIncorrectPreview() {
    val options = listOf("Opción A", "Opción B", "Opción C", "Opción D")
    RecallTheme {
        MultipleChoiceOptions(
            options = options,
            selectedOption = "Opción C",
            isAnswerCorrect = false,
            onOptionSelected = {}
        )
    }
}
