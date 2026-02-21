package com.fcorallini.recall.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme

private val RecallColorScheme = darkColorScheme(
    primary = RecallIndigo,
    secondary = RecallBlueSecondary,
    tertiary = RecallBluePrimary,
    background = RecallBackground,
    surface = RecallSurface,
    surfaceVariant = RecallSurfaceVariant,
    onPrimary = RecallOnSurface,
    onSecondary = RecallOnSurface,
    onTertiary = RecallOnSurface,
    onBackground = RecallOnBackground,
    onSurface = RecallOnSurface,
    onSurfaceVariant = RecallOnSurfaceVariant,
    outline = RecallOutline,
    error = RecallError
)

@Composable
fun RecallTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RecallColorScheme,
        typography = Typography,
        content = content
    )
}