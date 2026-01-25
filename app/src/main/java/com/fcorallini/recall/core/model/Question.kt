package com.fcorallini.recall.core.model

data class Question(
    val id: String,
    val sourceId: String,
    val type: QuestionType,
    val prompt: String,
    val options: List<String>,  // emptyList for flashcards
    val answer: String,         // correct answer text
    val stats: QuestionStats
)
