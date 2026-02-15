package com.fcorallini.recall.quiz.domain.usecase

import com.fcorallini.recall.core.data.common.Result
import com.fcorallini.recall.core.domain.repository.QuestionRepository
import javax.inject.Inject

class SubmitAnswerUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(questionId: String, userAnswer: String): Result<Unit> {
        return questionRepository.submitAnswer(questionId, userAnswer)
    }
}
