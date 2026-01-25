package com.fcorallini.recall.core.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Quiz : Route("quiz/{sourceId}") {
        fun createRoute(sourceId: String) = "quiz/$sourceId"
    }
}
