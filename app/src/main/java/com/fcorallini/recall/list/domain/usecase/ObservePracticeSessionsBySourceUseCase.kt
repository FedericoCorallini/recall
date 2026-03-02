package com.fcorallini.recall.list.domain.usecase

import com.fcorallini.recall.core.domain.model.PracticeSession
import com.fcorallini.recall.core.domain.repository.PracticeSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePracticeSessionsBySourceUseCase @Inject constructor(
    private val practiceSessionRepository: PracticeSessionRepository
) {
    operator fun invoke(sourceId: String): Flow<List<PracticeSession>> {
        return practiceSessionRepository.observeBySourceId(sourceId)
    }
}
