package com.fcorallini.recall.home.data.generator

import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.core.model.QuestionStats
import com.fcorallini.recall.core.model.QuestionType
import java.util.UUID

object MockQuestionGenerator {
    fun generateQuestions(sourceId: String): List<Question> {
        return listOf(
            // Multiple choice questions
            Question(
                id = UUID.randomUUID().toString(),
                sourceId = sourceId,
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "Which of the following is the correct way to launch a coroutine in Kotlin?",
                options = listOf(
                    "launch { }",
                    "async { }",
                    "runBlocking { }",
                    "All of the above"
                ),
                answer = "All of the above",
                stats = QuestionStats()
            ),
            Question(
                id = UUID.randomUUID().toString(),
                sourceId = sourceId,
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "What HTTP status code indicates a successful GET request?",
                options = listOf(
                    "200 OK",
                    "201 Created",
                    "204 No Content",
                    "404 Not Found"
                ),
                answer = "200 OK",
                stats = QuestionStats()
            ),
            Question(
                id = UUID.randomUUID().toString(),
                sourceId = sourceId,
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "In Clean Architecture, which layer contains business logic?",
                options = listOf(
                    "Presentation layer",
                    "Domain layer",
                    "Data layer",
                    "Infrastructure layer"
                ),
                answer = "Domain layer",
                stats = QuestionStats()
            ),
            Question(
                id = UUID.randomUUID().toString(),
                sourceId = sourceId,
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "Which Kotlin keyword is used to declare a read-only variable?",
                options = listOf(
                    "var",
                    "val",
                    "const",
                    "let"
                ),
                answer = "val",
                stats = QuestionStats()
            ),
            // Flashcard questions
            Question(
                id = UUID.randomUUID().toString(),
                sourceId = sourceId,
                type = QuestionType.FLASHCARD,
                prompt = "What does SOLID stand for in software engineering principles?",
                options = emptyList(),
                answer = "Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion",
                stats = QuestionStats()
            ),
            Question(
                id = UUID.randomUUID().toString(),
                sourceId = sourceId,
                type = QuestionType.FLASHCARD,
                prompt = "What is the purpose of a ViewModel in Android architecture?",
                options = emptyList(),
                answer = "To store and manage UI-related data in a lifecycle-conscious way",
                stats = QuestionStats()
            )
        )
    }
}
