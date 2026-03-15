package com.fcorallini.recall.core.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RecallBottomNavigationBar(
    isHomeSelected: Boolean,
    isListSelected: Boolean,
    onHomeClick: () -> Unit,
    onAddPdfClick: () -> Unit,
    onListClick: () -> Unit,
    isAddPdfEnabled: Boolean = true
) {
    NavigationBar {
        NavigationBarItem(
            selected = isHomeSelected,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onAddPdfClick,
            icon = { Icon(Icons.Default.Add, contentDescription = "Add PDF") },
            label = { Text("Add PDF") },
            enabled = isAddPdfEnabled
        )

        NavigationBarItem(
            selected = isListSelected,
            onClick = onListClick,
            icon = { Icon(Icons.Default.List, contentDescription = "Quizzes") },
            label = { Text("Quizzes") }
        )
    }
}
