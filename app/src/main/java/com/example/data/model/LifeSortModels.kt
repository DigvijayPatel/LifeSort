package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class EnergyLevel(val label: String, val weight: Float) {
    LOW("Low Energy", 1.0f),
    MEDIUM("Medium Energy", 1.2f),
    HIGH("High Energy", 1.5f);

    companion object {
        fun fromString(value: String): EnergyLevel {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}

enum class TaskCategory(val label: String, val symbol: String) {
    SPIRITUAL("Spiritual", "✨"),
    CREATIVE("Creative", "🎨"),
    HEALTH("Health", "🌿"),
    WORK("Work", "⚡"),
    PERSONAL("Personal", "🌌"),
    RELATIONAL("Relational", "💫");

    companion object {
        fun fromString(value: String): TaskCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: PERSONAL
        }
    }
}

enum class TaskStatus {
    BACKLOG,
    TODAY,
    IN_PROGRESS,
    PAUSED,
    COMPLETED
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val clarityScore: Float = 70f,      // 0 - 100
    val impactScore: Float = 70f,       // 0 - 100
    val urgencyScore: Float = 50f,      // 0 - 100
    val energyLevel: String = EnergyLevel.MEDIUM.name,
    val estimatedMinutes: Int = 25,
    val actualMinutesSpent: Int = 0,
    val category: String = TaskCategory.PERSONAL.name,
    val status: String = TaskStatus.TODAY.name,
    val computedRank: Float = 0f,
    val aiRationale: String = "",
    val interruptionReason: String? = null,
    val pausedAt: Long? = null,
    val completedAt: Long? = null,
    val scheduledAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncedToSupabase: Boolean = false
)

@Entity(
    tableName = "phases",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class TaskPhaseEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val title: String,
    val orderIndex: Int = 0,
    val targetMinutes: Int = 15,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "task_notes")
data class TaskNoteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val taskId: String? = null,
    val content: String,
    val reflectionMood: String = "Peaceful",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_growth_history")
data class LifeGrowthSnapshotEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val growthScore: Int,
    val mindfulFocusScore: Int,
    val executionHarmonyScore: Int,
    val mentalClarityScore: Int,
    val cosmicMomentumScore: Int,
    val dayLabel: String,
    val eventNote: String = ""
)
