package com.fcorallini.recall.quiz.domain.usecase

import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveQuestionsBySourceUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(sourceId: String): Flow<List<Question>> {
        return repository.observeQuestionsBySourceId(sourceId)
    }
}
