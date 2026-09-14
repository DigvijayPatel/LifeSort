package com.example.domain.gamification

import androidx.compose.ui.graphics.Color
import com.example.data.model.TaskEntity
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBluePrimary
import com.example.ui.theme.MangoDeep
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.MysticCyan
import com.example.ui.theme.SacredEmerald

/**
 * Personal Life RPG Level Definition
 */
data class LifeLevel(
    val level: Int,
    val title: String,
    val minScore: Int,
    val maxScore: Int,
    val ringsCount: Int,
    val particleCount: Int,
    val auraColor: Color,
    val description: String
)

/**
 * Milestone unlocked through conscious action
 */
data class LifeMilestone(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: Float = 1.0f // 0.0 to 1.0
)

/**
 * Reward outcome emitted upon completing a task or focus session
 */
data class CompletionReward(
    val xpGained: Int,
    val previousScore: Int,
    val newScore: Int,
    val leveledUp: Boolean,
    val newLevelTitle: String?,
    val unlockedMilestone: LifeMilestone?,
    val taskTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

object LifeRpgEngine {

    val LEVELS = listOf(
        LifeLevel(
            level = 1,
            title = "Seeker of Stillness",
            minScore = 0,
            maxScore = 300,
            ringsCount = 1,
            particleCount = 6,
            auraColor = MysticCyan,
            description = "Beginning the inner journey. Calming the scattered mind through single-pointed focus."
        ),
        LifeLevel(
            level = 2,
            title = "Initiate of Flow",
            minScore = 301,
            maxScore = 500,
            ringsCount = 2,
            particleCount = 8,
            auraColor = MangoWarm,
            description = "Momentum takes root. Actions move with ease and rhythm."
        ),
        LifeLevel(
            level = 3,
            title = "Conscious Architect",
            minScore = 501,
            maxScore = 700,
            ringsCount = 3,
            particleCount = 10,
            auraColor = CelestialViolet,
            description = "Aligning daily effort with celestial clarity. Designing life with intention."
        ),
        LifeLevel(
            level = 4,
            title = "Flow Harmonizer",
            minScore = 701,
            maxScore = 840,
            ringsCount = 4,
            particleCount = 12,
            auraColor = SacredEmerald,
            description = "Effortless mastery. Deep presence balances high impact and inner peace."
        ),
        LifeLevel(
            level = 5,
            title = "Master of Mind",
            minScore = 841,
            maxScore = 940,
            ringsCount = 5,
            particleCount = 16,
            auraColor = MangoDeep,
            description = "Stillness in action. The mind is a brilliant instrument of boundless creation."
        ),
        LifeLevel(
            level = 6,
            title = "Cosmic Sovereign",
            minScore = 941,
            maxScore = 1000,
            ringsCount = 6,
            particleCount = 20,
            auraColor = Color(0xFFFFD700), // Radiant Pure Gold
            description = "Transcendent unity. Every conscious breath elevates the universe around you."
        )
    )

    fun getLevelForScore(score: Int): LifeLevel {
        val clamped = score.coerceIn(0, 1000)
        return LEVELS.firstOrNull { clamped in it.minScore..it.maxScore } ?: LEVELS.last()
    }

    fun getProgressToNextLevel(score: Int): Float {
        val current = getLevelForScore(score)
        if (current.level == LEVELS.last().level) return 1.0f
        val range = (current.maxScore - current.minScore).toFloat().coerceAtLeast(1f)
        val progress = (score - current.minScore) / range
        return progress.coerceIn(0.0f, 1.0f)
    }

    /**
     * Complete catalog of all milestones
     */
    val ALL_MILESTONES: List<LifeMilestone> = listOf(
        LifeMilestone(
            id = "first_step",
            title = "First Conscious Step",
            subtitle = "Complete 1 conscious action",
            icon = "✨",
            isUnlocked = false,
            progress = 0f
        ),
        LifeMilestone(
            id = "sustained_flame",
            title = "Sustained Flame",
            subtitle = "Maintain a 3-day conscious streak",
            icon = "🔥",
            isUnlocked = false,
            progress = 0f
        ),
        LifeMilestone(
            id = "deep_stillness",
            title = "Hour of Stillness",
            subtitle = "Accumulate 60 minutes of focused flow",
            icon = "🧘",
            isUnlocked = false,
            progress = 0f
        ),
        LifeMilestone(
            id = "clarity_adept",
            title = "Clarity Adept",
            subtitle = "Complete 5 conscious actions",
            icon = "⚡",
            isUnlocked = false,
            progress = 0f
        ),
        LifeMilestone(
            id = "vitality_surge",
            title = "Vitality Surge",
            subtitle = "Reach 750+ Life Growth Score",
            icon = "🌌",
            isUnlocked = false,
            progress = 0f
        ),
        LifeMilestone(
            id = "cosmic_centurion",
            title = "Mastery Ascendant",
            subtitle = "Accumulate 180 minutes of pure focus",
            icon = "👑",
            isUnlocked = false,
            progress = 0f
        )
    )

    /**
     * Evaluates all milestones based on cumulative stats
     */
    fun evaluateMilestones(
        completedCount: Int,
        totalFocusMinutes: Int,
        activeStreakDays: Int,
        growthScore: Int
    ): List<LifeMilestone> {
        return listOf(
            LifeMilestone(
                id = "first_step",
                title = "First Conscious Step",
                subtitle = "Complete 1 conscious action",
                icon = "✨",
                isUnlocked = completedCount >= 1,
                progress = (completedCount / 1f).coerceIn(0f, 1f)
            ),
            LifeMilestone(
                id = "sustained_flame",
                title = "Sustained Flame",
                subtitle = "Maintain a 3-day conscious streak",
                icon = "🔥",
                isUnlocked = activeStreakDays >= 3,
                progress = (activeStreakDays / 3f).coerceIn(0f, 1f)
            ),
            LifeMilestone(
                id = "deep_stillness",
                title = "Hour of Stillness",
                subtitle = "Accumulate 60 minutes of focused flow",
                icon = "🧘",
                isUnlocked = totalFocusMinutes >= 60,
                progress = (totalFocusMinutes / 60f).coerceIn(0f, 1f)
            ),
            LifeMilestone(
                id = "clarity_adept",
                title = "Clarity Adept",
                subtitle = "Complete 5 conscious actions",
                icon = "⚡",
                isUnlocked = completedCount >= 5,
                progress = (completedCount / 5f).coerceIn(0f, 1f)
            ),
            LifeMilestone(
                id = "vitality_surge",
                title = "Vitality Surge",
                subtitle = "Reach 750+ Life Growth Score",
                icon = "🌌",
                isUnlocked = growthScore >= 750,
                progress = (growthScore / 750f).coerceIn(0f, 1f)
            ),
            LifeMilestone(
                id = "cosmic_centurion",
                title = "Mastery Ascendant",
                subtitle = "Accumulate 180 minutes of pure focus",
                icon = "👑",
                isUnlocked = totalFocusMinutes >= 180,
                progress = (totalFocusMinutes / 180f).coerceIn(0f, 1f)
            )
        )
    }

    /**
     * Computes the rewarding celebration payload when a task completes
     */
    fun computeReward(
        task: TaskEntity,
        previousScore: Int,
        minutesSpent: Int,
        currentCompletedCount: Int,
        activeStreakDays: Int,
        totalFocusMinutes: Int
    ): CompletionReward {
        // Base XP: 35 + scaled by impact and clarity
        val impactBonus = (task.impactScore * 0.25f).toInt()
        val clarityBonus = (task.clarityScore * 0.15f).toInt()
        val durationBonus = (minutesSpent * 1.2f).toInt()
        val xpGained = (35 + impactBonus + clarityBonus + durationBonus).coerceIn(40, 95)

        val newScore = (previousScore + xpGained).coerceAtMost(1000)

        val prevLevel = getLevelForScore(previousScore)
        val newLevel = getLevelForScore(newScore)
        val leveledUp = newLevel.level > prevLevel.level

        // Check if any milestone was newly unlocked
        val prevMilestones = evaluateMilestones(
            currentCompletedCount,
            totalFocusMinutes,
            activeStreakDays,
            previousScore
        )
        val newMilestones = evaluateMilestones(
            currentCompletedCount + 1,
            totalFocusMinutes + minutesSpent,
            activeStreakDays,
            newScore
        )
        val newlyUnlocked = newMilestones.firstOrNull { newM ->
            newM.isUnlocked && prevMilestones.none { it.id == newM.id && it.isUnlocked }
        }

        return CompletionReward(
            xpGained = xpGained,
            previousScore = previousScore,
            newScore = newScore,
            leveledUp = leveledUp,
            newLevelTitle = if (leveledUp) newLevel.title else null,
            unlockedMilestone = newlyUnlocked,
            taskTitle = task.title
        )
    }
}
