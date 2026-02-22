package com.fcorallini.recall.home.domain.usecase
 
import com.fcorallini.recall.core.data.common.TimeProvider
import com.fcorallini.recall.core.domain.model.GlobalStats
import com.fcorallini.recall.core.domain.model.PracticeSession
import com.fcorallini.recall.core.domain.repository.PracticeSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
 
class ObserveGlobalStatsUseCase @Inject constructor(
    private val practiceSessionRepository: PracticeSessionRepository,
    private val timeProvider: TimeProvider
) {
    operator fun invoke(): Flow<GlobalStats> {
        return practiceSessionRepository.observeAll().map { sessions ->
            buildGlobalStats(sessions)
        }
    }
 
    private fun buildGlobalStats(sessions: List<PracticeSession>): GlobalStats {
        if (sessions.isEmpty()) {
            return GlobalStats()
        }
 
        val totalPractices = sessions.size
        val averageScore = sessions.map { it.score }.average().toFloat()
        val lastPracticedEpochMs = sessions.maxOf { it.completedAtEpochMs }
        val streakDays = calculateStreakDays(sessions)
 
        return GlobalStats(
            streakDays = streakDays,
            totalPractices = totalPractices,
            averageScore = averageScore,
            lastPracticedEpochMs = lastPracticedEpochMs
        )
    }
 
    private fun calculateStreakDays(sessions: List<PracticeSession>): Int {
        val daysPracticed = sessions
            .map { TimeUnit.MILLISECONDS.toDays(it.completedAtEpochMs) }
            .toSet()
        if (daysPracticed.isEmpty()) return 0
 
        var day = TimeUnit.MILLISECONDS.toDays(timeProvider.currentTimeMillis())
        if (!daysPracticed.contains(day)) return 0
 
        var streak = 0
        while (daysPracticed.contains(day)) {
            streak++
            day -= 1
        }
        return streak
    }
}