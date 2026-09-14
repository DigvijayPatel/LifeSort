package com.example.domain.engine

import com.example.data.model.EnergyLevel
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPhaseEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

data class Recommendation(
    val task: TaskEntity,
    val rationale: String,
    val urgencyLabel: String,
    val energyHarmony: String
)

object DeterministicRankingEngine {

    /**
     * Deterministically calculates the priority score (0 - 100+) of a task.
     * Weights:
     * - Impact: 42%
     * - Urgency: 38%
     * - Clarity: 15%
     * - Energy alignment: 5%
     */
    fun calculateTaskRank(
        impactScore: Float,
        urgencyScore: Float,
        clarityScore: Float,
        energyLevel: EnergyLevel
    ): Float {
        val clampedImpact = impactScore.coerceIn(0f, 100f)
        val clampedUrgency = urgencyScore.coerceIn(0f, 100f)
        val clampedClarity = clarityScore.coerceIn(0f, 100f)
        val energyBonus = (energyLevel.weight - 1.0f) * 10f // 0 to 5

        return (clampedImpact * 0.42f) + (clampedUrgency * 0.38f) + (clampedClarity * 0.15f) + energyBonus
    }

    /**
     * Deterministically selects the single "What should I do now?" task.
     * Takes currently active/paused tasks, sorts them, and determines the most potent focus action.
     */
    fun determineWhatToDoNow(
        tasks: List<TaskEntity>,
        phasesMap: Map<String, List<TaskPhaseEntity>> = emptyMap()
    ): Recommendation? {
        val eligibleTasks = tasks.filter { it.status != "COMPLETED" }
        if (eligibleTasks.isEmpty()) return null

        // If there's an interrupted/paused task, suggest resuming it with high priority
        val pausedTask = eligibleTasks.firstOrNull { it.status == "PAUSED" }
        if (pausedTask != null) {
            val pausedReason = pausedTask.interruptionReason?.let { " ($it)" } ?: ""
            return Recommendation(
                task = pausedTask,
                rationale = "Resume your sacred flow: This task was gently paused$pausedReason. Restoring momentum brings effortless mastery.",
                urgencyLabel = "Resume Interrupted Flow",
                energyHarmony = "Reconnection Ready"
            )
        }

        // Sort by computed rank descending, then by impact
        val rankedTasks = eligibleTasks.sortedWith(
            compareByDescending<TaskEntity> { it.computedRank }
                .thenByDescending { it.impactScore }
                .thenBy { it.estimatedMinutes }
        )

        val topTask = rankedTasks.first()
        val phases = phasesMap[topTask.id] ?: emptyList()
        val nextIncompletePhase = phases.firstOrNull { !it.isCompleted }

        val rationale = if (nextIncompletePhase != null) {
            "Cosmic Alignment: Begin with Phase '${nextIncompletePhase.title}'. Small conscious steps transmute inertia into ecstasy."
        } else if (topTask.impactScore >= 80f) {
            "High Celestial Impact: Completing this frees massive mental bandwidth and elevates your Life Vitality immediately."
        } else if (topTask.urgencyScore >= 75f) {
            "Timely Clarity: Address this current priority to restore serenity and flow to your day."
        } else {
            "Conscious Flow: Single-point focus on this action aligns your day with grounded progress."
        }

        val urgencyLabel = when {
            topTask.urgencyScore >= 75f -> "Immediate Flow"
            topTask.impactScore >= 80f -> "Core Catalyst"
            else -> "Mindful Priority"
        }

        val energyHarmony = when (EnergyLevel.fromString(topTask.energyLevel)) {
            EnergyLevel.LOW -> "Gentle Water Flow"
            EnergyLevel.MEDIUM -> "Balanced Solar Fire"
            EnergyLevel.HIGH -> "Peak Creative Fire"
        }

        return Recommendation(
            task = topTask,
            rationale = rationale,
            urgencyLabel = urgencyLabel,
            energyHarmony = energyHarmony
        )
    }
}

object DeterministicLifeGrowthEngine {

    /**
     * Calculates the deterministic Life Growth score components (0 to 1000 total).
     *
     * Pillar 1: Mindful Focus (0-300)
     * Based on focused minutes spent in deep work without scatteredness.
     * (e.g. 150 mins = ~200 pts)
     *
     * Pillar 2: Execution Harmony (0-300)
     * Based on completed tasks and completed phases.
     *
     * Pillar 3: Mental Clarity (0-200)
     * Based on average clarity score of sorted tasks and frequency of brain dumping.
     *
     * Pillar 4: Cosmic Momentum (0-200)
     * Baseline + action streak points.
     */
    fun computeLifeGrowthScore(
        totalFocusMinutes: Int,
        completedTasksCount: Int,
        totalTasksCount: Int,
        completedPhasesCount: Int,
        totalPhasesCount: Int,
        averageClarity: Float,
        activeStreakDays: Int = 1
    ): LifeGrowthSnapshotEntity {
        // Mindful Focus: 2 points per focus minute, capped at 300
        val focusScore = min(300, (totalFocusMinutes * 2.2f).roundToInt()).coerceAtLeast(30)

        // Execution Harmony: ratio of completion + absolute completions
        val taskRatio = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0.5f
        val phaseRatio = if (totalPhasesCount > 0) completedPhasesCount.toFloat() / totalPhasesCount else 0.5f
        val harmonyBase = ((taskRatio * 0.5f + phaseRatio * 0.5f) * 200f) + (completedTasksCount * 15f)
        val harmonyScore = min(300, harmonyBase.roundToInt()).coerceAtLeast(40)

        // Mental Clarity: clarity score scaled to 200
        val clarityScore = min(200, (averageClarity.coerceIn(40f, 100f) * 1.9f).roundToInt()).coerceAtLeast(50)

        // Cosmic Momentum: streak days and daily action
        val momentumScore = min(200, (activeStreakDays * 25 + 60)).coerceAtLeast(50)

        val totalGrowth = min(1000, focusScore + harmonyScore + clarityScore + momentumScore)

        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        val dayLabel = dateFormat.format(Date())

        return LifeGrowthSnapshotEntity(
            timestamp = System.currentTimeMillis(),
            growthScore = totalGrowth,
            mindfulFocusScore = focusScore,
            executionHarmonyScore = harmonyScore,
            mentalClarityScore = clarityScore,
            cosmicMomentumScore = momentumScore,
            dayLabel = dayLabel,
            eventNote = "Conscious Action Progression"
        )
    }
}
