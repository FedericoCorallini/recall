package com.fcorallini.recall.core.presentation.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object List : Route("list")
    data object Quiz : Route("quiz/{sourceId}") {
        fun createRoute(sourceId: String) = "quiz/$sourceId"
    }
}
