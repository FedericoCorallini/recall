package com.fcorallini.recall.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fcorallini.recall.home.presentation.HomeScreen
import com.fcorallini.recall.quiz.presentation.QuizScreen

@Composable
fun RecallNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.path
    ) {
        composable(Route.Home.path) {
            HomeScreen(
                onNavigateToQuiz = { sourceId ->
                    navController.navigate(Route.Quiz.createRoute(sourceId))
                }
            )
        }

        composable(
            route = Route.Quiz.path,
            arguments = listOf(
                navArgument("sourceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sourceId = backStackEntry.arguments?.getString("sourceId") ?: return@composable
            QuizScreen(
                sourceId = sourceId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
