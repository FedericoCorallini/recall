package com.fcorallini.recall.quiz.domain.usecase

import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.home.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveQuestionsBySourceUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(sourceId: String): Flow<List<Question>> {
        return questionRepository.observeBySourceId(sourceId)
    }
}
