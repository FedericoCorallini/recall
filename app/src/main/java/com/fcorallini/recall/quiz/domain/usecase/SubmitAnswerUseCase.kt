package com.fcorallini.recall.quiz.domain.usecase

import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.quiz.domain.repository.QuizRepository
import javax.inject.Inject

class SubmitAnswerUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(questionId: String, userAnswer: String): Result<Unit> {
        return repository.submitAnswer(questionId, userAnswer)
    }
}
