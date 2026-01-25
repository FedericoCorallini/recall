package com.fcorallini.recall.quiz.domain.usecase

import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.quiz.domain.repository.QuizRepository
import javax.inject.Inject

class UpdatePdfSourceStatsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(
        sourceId: String,
        correctCount: Int,
        totalCount: Int
    ): Result<Unit> {
        val score = if (totalCount > 0) correctCount.toFloat() / totalCount.toFloat() else 0f
        return repository.updatePdfSourceStats(sourceId, score)
    }
}
