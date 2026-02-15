package com.fcorallini.recall.core.domain.model

data class Question(
    val id: String,
    val sourceId: String,
    val type: QuestionType,
    val prompt: String,
    val options: List<String>,
    val answer: String,
    val stats: QuestionStats
)
