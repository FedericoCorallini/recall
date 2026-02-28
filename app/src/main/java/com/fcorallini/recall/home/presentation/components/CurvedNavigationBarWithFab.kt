package com.fcorallini.recall.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fcorallini.recall.core.presentation.theme.RecallTheme

class CurvedNavBarShape(
    private val cornerRadius: Float,
    private val dockRadius: Float,
) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): androidx.compose.ui.graphics.Outline {
        // Base rect with rounded top corners
        val baseRect = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, Offset(size.width, size.height)),
                    topLeft = CornerRadius(cornerRadius, cornerRadius),
                    topRight = CornerRadius(cornerRadius, cornerRadius),
                )
            )
        }

        // Circle cutout at center (for FAB)
        val circle = Path().apply {
            addOval(
                Rect(
                    center = Offset(size.width / 2, 0f),
                    radius = dockRadius,
                )
            )
        }

        // Rect1: Left section
        val rect1 = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, Offset(size.width / 2 - dockRadius + 4f, size.height)),
                    topLeft = CornerRadius(cornerRadius, cornerRadius),
                )
            )
        }

        // rect1A with smaller topRight radius for smooth curve
        val rect1A = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, Offset(size.width / 2 - dockRadius + 4f, size.height)),
                    topLeft = CornerRadius(cornerRadius, cornerRadius),
                    topRight = CornerRadius(32f, 32f),
                )
            )
        }

        val rect1B = Path.combine(
            operation = PathOperation.Difference,
            path1 = rect1,
            path2 = rect1A,
        )

        // Rect2: Right section
        val rect2 = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset(size.width / 2 + dockRadius - 4f, 0f), Offset(size.width, size.height)),
                    topRight = CornerRadius(cornerRadius, cornerRadius),
                )
            )
        }

        // rect2A with smaller topLeft radius
        val rect2A = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset(size.width / 2 + dockRadius - 4f, 0f), Offset(size.width, size.height)),
                    topRight = CornerRadius(cornerRadius, cornerRadius),
                    topLeft = CornerRadius(32f, 32f),
                )
            )
        }

        val rect2B = Path.combine(
            operation = PathOperation.Difference,
            path1 = rect2,
            path2 = rect2A,
        )

        // Combine all paths
        val path1 = Path.combine(
            operation = PathOperation.Difference,
            path1 = baseRect,
            path2 = circle,
        )

        val path2 = Path.combine(
            operation = PathOperation.Difference,
            path1 = path1,
            path2 = rect1B,
        )

        val finalPath = Path.combine(
            operation = PathOperation.Difference,
            path1 = path2,
            path2 = rect2B,
        )

        return androidx.compose.ui.graphics.Outline.Generic(finalPath)
    }
}

@Composable
fun CurvedNavigationBarWithFab(
    isHomeSelected: Boolean,
    isListSelected: Boolean,
    onHomeClick: () -> Unit,
    onListClick: () -> Unit,
    onFabClick: () -> Unit,
    showFab: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val cornerRadius = 20.dp
    val dockRadius = 38.dp
    val navBarHeight = 80.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(navBarHeight + 28.dp) // Extra space for FAB
    ) {
        // Navigation bar with cutout
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(navBarHeight)
                .clip(CurvedNavBarShape(
                    cornerRadius = with(LocalDensity.current) { cornerRadius.toPx() },
                    dockRadius = with(LocalDensity.current) { dockRadius.toPx() },
                ))
                .background(Color(0xFF1A1A1A).copy(alpha = 0.95f))
                .shadow(8.dp, shape = CurvedNavBarShape(
                    cornerRadius = with(LocalDensity.current) { cornerRadius.toPx() },
                    dockRadius = with(LocalDensity.current) { dockRadius.toPx() },
                ))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home item
                NavigationBarItem(
                    selected = isHomeSelected,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                // Spacer for FAB
                Spacer(modifier = Modifier.weight(1f))

                // List item
                NavigationBarItem(
                    selected = isListSelected,
                    onClick = onListClick,
                    icon = { Icon(Icons.Default.List, contentDescription = "Quizzes") },
                    label = { Text("Quizzes") }
                )
            }
        }

        // Centered FAB
        if (showFab) {
            FloatingActionButton(
                onClick = onFabClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(56.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add PDF")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurvedNavigationBarWithFabPreview() {
    RecallTheme {
        CurvedNavigationBarWithFab(
            isHomeSelected = true,
            isListSelected = false,
            onHomeClick = {},
            onListClick = {},
            onFabClick = {},
            showFab = true
        )
    }
}
