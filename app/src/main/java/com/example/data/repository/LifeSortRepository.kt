package com.example.data.repository

import android.content.Context
import com.example.data.local.LifeSortDatabase
import com.example.data.model.EnergyLevel
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity
import com.example.data.model.TaskStatus
import com.example.data.remote.ExtractedTaskDto
import com.example.data.remote.SupabaseClient
import com.example.domain.engine.DeterministicLifeGrowthEngine
import com.example.domain.engine.DeterministicRankingEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class LifeSortRepository(
    private val database: LifeSortDatabase,
    val supabaseClient: SupabaseClient
) {
    private val taskDao = database.taskDao()
    private val phaseDao = database.phaseDao()
    private val noteDao = database.noteDao()
    private val lifeGrowthDao = database.lifeGrowthDao()

    val activeTasks: Flow<List<TaskEntity>> = taskDao.getActiveTasks()
    val completedTasks: Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    val deletedTasks: Flow<List<TaskEntity>> = taskDao.getDeletedTasks()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val latestPausedTask: Flow<TaskEntity?> = taskDao.getLatestPausedTask()
    val activeRunningTask: Flow<TaskEntity?> = taskDao.getActiveRunningTask()
    val allNotes: Flow<List<TaskNoteEntity>> = noteDao.getAllNotes()
    val growthHistory: Flow<List<LifeGrowthSnapshotEntity>> = lifeGrowthDao.getGrowthHistory()
    val latestSnapshot: Flow<LifeGrowthSnapshotEntity?> = lifeGrowthDao.getLatestSnapshot()
    val allPhases: Flow<List<TaskPhaseEntity>> = phaseDao.getAllPhases()

    fun getPhasesForTask(taskId: String): Flow<List<TaskPhaseEntity>> = phaseDao.getPhasesForTask(taskId)
    fun getNotesForTask(taskId: String): Flow<List<TaskNoteEntity>> = noteDao.getNotesForTask(taskId)

    suspend fun getTaskById(taskId: String): TaskEntity? = taskDao.getTaskById(taskId)

    suspend fun insertTaskWithPhases(
        title: String,
        description: String,
        clarityScore: Float,
        impactScore: Float,
        urgencyScore: Float,
        energyLevel: EnergyLevel,
        estimatedMinutes: Int,
        category: TaskCategory,
        phasesList: List<String>,
        aiRationale: String
    ): TaskEntity = withContext(Dispatchers.IO) {
        val rank = DeterministicRankingEngine.calculateTaskRank(impactScore, urgencyScore, clarityScore, energyLevel)
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            clarityScore = clarityScore,
            impactScore = impactScore,
            urgencyScore = urgencyScore,
            energyLevel = energyLevel.name,
            estimatedMinutes = estimatedMinutes,
            category = category.name,
            status = TaskStatus.TODAY.name,
            computedRank = rank,
            aiRationale = aiRationale
        )
        taskDao.insertTask(task)

        val phases = phasesList.mapIndexed { index, phaseTitle ->
            TaskPhaseEntity(
                id = UUID.randomUUID().toString(),
                taskId = task.id,
                title = phaseTitle,
                orderIndex = index,
                targetMinutes = (estimatedMinutes / (phasesList.size.coerceAtLeast(1))).coerceAtLeast(5)
            )
        }
        if (phases.isNotEmpty()) {
            phaseDao.insertPhases(phases)
        }

        supabaseClient.syncTask(task)
        recalculateAndSaveGrowthSnapshot("Added task: ${task.title}")
        task
    }

    suspend fun saveExtractedTasks(extractedList: List<ExtractedTaskDto>) = withContext(Dispatchers.IO) {
        for (item in extractedList) {
            insertTaskWithPhases(
                title = item.title,
                description = item.description,
                clarityScore = item.clarityScore,
                impactScore = item.impactScore,
                urgencyScore = item.urgencyScore,
                energyLevel = item.energyLevel,
                estimatedMinutes = item.estimatedMinutes,
                category = item.category,
                phasesList = item.phases,
                aiRationale = item.aiRationale
            )
        }
    }

    suspend fun startTaskTimer(taskId: String) = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskById(taskId) ?: return@withContext
        val updated = task.copy(
            status = TaskStatus.IN_PROGRESS.name,
            interruptionReason = null,
            pausedAt = null,
            updatedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(updated)
        supabaseClient.syncTask(updated)
    }

    suspend fun pauseTaskTimer(taskId: String, minutesSpent: Int, reason: String) = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskById(taskId) ?: return@withContext
        val updated = task.copy(
            status = TaskStatus.PAUSED.name,
            actualMinutesSpent = task.actualMinutesSpent + minutesSpent,
            interruptionReason = reason,
            pausedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(updated)
        supabaseClient.syncTask(updated)
    }

    suspend fun resumeTaskTimer(taskId: String) = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskById(taskId) ?: return@withContext
        val updated = task.copy(
            status = TaskStatus.IN_PROGRESS.name,
            interruptionReason = null,
            pausedAt = null,
            updatedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(updated)
        supabaseClient.syncTask(updated)
    }

    suspend fun completeTask(taskId: String, finalMinutesSpent: Int, reflectionNote: String? = null) = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskById(taskId) ?: return@withContext
        val updated = task.copy(
            status = TaskStatus.COMPLETED.name,
            actualMinutesSpent = task.actualMinutesSpent + finalMinutesSpent,
            completedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(updated)

        // Mark all phases completed if any remain
        val phases = phaseDao.getPhasesForTaskSync(taskId)
        for (phase in phases) {
            if (!phase.isCompleted) {
                phaseDao.updatePhase(phase.copy(isCompleted = true, completedAt = System.currentTimeMillis()))
            }
        }

        if (!reflectionNote.isNullOrBlank()) {
            noteDao.insertNote(
                TaskNoteEntity(
                    taskId = taskId,
                    content = reflectionNote,
                    reflectionMood = "Ecstatic"
                )
            )
        }

        supabaseClient.syncTask(updated)
        recalculateAndSaveGrowthSnapshot("Completed task: ${task.title}")
    }

    suspend fun togglePhase(phaseId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        val phases = phaseDao.getAllPhases().first()
        val phase = phases.firstOrNull { it.id == phaseId } ?: return@withContext
        val updated = phase.copy(
            isCompleted = isCompleted,
            completedAt = if (isCompleted) System.currentTimeMillis() else null
        )
        phaseDao.updatePhase(updated)
        recalculateAndSaveGrowthSnapshot("Phase progress updated")
    }

    suspend fun addPhaseToTask(taskId: String, title: String, targetMinutes: Int) = withContext(Dispatchers.IO) {
        val existing = phaseDao.getPhasesForTaskSync(taskId)
        val phase = TaskPhaseEntity(
            taskId = taskId,
            title = title,
            orderIndex = existing.size,
            targetMinutes = targetMinutes
        )
        phaseDao.insertPhase(phase)
    }

    suspend fun addNote(taskId: String?, content: String, mood: String) = withContext(Dispatchers.IO) {
        val note = TaskNoteEntity(
            taskId = taskId,
            content = content,
            reflectionMood = mood
        )
        noteDao.insertNote(note)
    }

    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskById(taskId)
        if (task != null) {
            taskDao.updateTask(task.copy(status = "DELETED", updatedAt = System.currentTimeMillis()))
        }
        recalculateAndSaveGrowthSnapshot("Task removed")
    }

    suspend fun restoreTask(taskId: String) = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskById(taskId)
        if (task != null) {
            taskDao.updateTask(task.copy(status = "TODAY", updatedAt = System.currentTimeMillis()))
        }
        recalculateAndSaveGrowthSnapshot("Task restored")
    }

    suspend fun recalculateAndSaveGrowthSnapshot(reason: String = "") = withContext(Dispatchers.IO) {
        val all = taskDao.getAllTasks().first()
        val allPh = phaseDao.getAllPhases().first()
        val completed = all.filter { it.status == "COMPLETED" }
        val totalFocusMinutes = all.sumOf { it.actualMinutesSpent }
        val completedPhases = allPh.count { it.isCompleted }
        val avgClarity = if (all.isNotEmpty()) all.map { it.clarityScore }.average().toFloat() else 75f

        val snapshot = DeterministicLifeGrowthEngine.computeLifeGrowthScore(
            totalFocusMinutes = totalFocusMinutes,
            completedTasksCount = completed.size,
            totalTasksCount = all.size,
            completedPhasesCount = completedPhases,
            totalPhasesCount = allPh.size,
            averageClarity = avgClarity,
            activeStreakDays = 5
        ).copy(eventNote = reason)

        lifeGrowthDao.insertSnapshot(snapshot)
        val allSnaps = lifeGrowthDao.getGrowthHistory().first()
        supabaseClient.syncSnapshots(allSnaps)
    }

    /**
     * Seeds realistic cosmic data to demonstrate full production experience:
     * - Realistic contemplative & high-impact tasks
     * - Structured sub-phases
     * - Interrupted task with reason
     * - Notes with moods
     * - 7-day realistic historical Life Growth snapshots
     */
    suspend fun seedRealisticCosmicData() = withContext(Dispatchers.IO) {
        taskDao.clearAllTasks()
        phaseDao.clearAllPhases()
        noteDao.clearAllNotes()
        lifeGrowthDao.clearAllHistory()

        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        // 1. Task 1: Interrupted task (for testing and showcasing resume interrupted task)
        val task1 = TaskEntity(
            id = "seed-task-interrupted",
            title = "Architect Life Sort Cosmic Engine",
            description = "Map out the deterministic scoring, breathing orb canvas, and Supabase cloud sync protocol.",
            clarityScore = 92f,
            impactScore = 95f,
            urgencyScore = 88f,
            energyLevel = EnergyLevel.HIGH.name,
            estimatedMinutes = 45,
            actualMinutesSpent = 22,
            category = TaskCategory.CREATIVE.name,
            status = TaskStatus.PAUSED.name,
            computedRank = DeterministicRankingEngine.calculateTaskRank(95f, 88f, 92f, EnergyLevel.HIGH),
            aiRationale = "Core creative catalyst: Solidifying the architecture unleashes effortless building momentum.",
            interruptionReason = "Phone call with client",
            pausedAt = now - (35 * 60 * 1000L)
        )
        taskDao.insertTask(task1)
        phaseDao.insertPhases(
            listOf(
                TaskPhaseEntity(taskId = task1.id, title = "Define Room DB and DAOs", orderIndex = 0, targetMinutes = 15, isCompleted = true, completedAt = now - 40 * 60 * 1000L),
                TaskPhaseEntity(taskId = task1.id, title = "Implement Breathing Orb Canvas", orderIndex = 1, targetMinutes = 20, isCompleted = false),
                TaskPhaseEntity(taskId = task1.id, title = "Integrate Gemini & Supabase", orderIndex = 2, targetMinutes = 10, isCompleted = false)
            )
        )
        noteDao.insertNote(
            TaskNoteEntity(
                taskId = task1.id,
                content = "Felt deep cosmic flow while designing the breathing orb. Need to ensure radial glow breathes in sync with diaphragm.",
                reflectionMood = "Focused",
                createdAt = now - 36 * 60 * 1000L
            )
        )

        // 2. Task 2: High Impact Spiritual Task
        val task2 = TaskEntity(
            id = "seed-task-spiritual",
            title = "Morning Shambhavi Mahamudra Kriya",
            description = "21 minutes of consecrated breathing, inner stillness, and cosmic alignment before work begins.",
            clarityScore = 98f,
            impactScore = 96f,
            urgencyScore = 80f,
            energyLevel = EnergyLevel.LOW.name,
            estimatedMinutes = 25,
            actualMinutesSpent = 0,
            category = TaskCategory.SPIRITUAL.name,
            status = TaskStatus.TODAY.name,
            computedRank = DeterministicRankingEngine.calculateTaskRank(96f, 80f, 98f, EnergyLevel.LOW),
            aiRationale = "When inner vitality is organized, all outer activity occurs with effortless exuberance."
        )
        taskDao.insertTask(task2)
        phaseDao.insertPhases(
            listOf(
                TaskPhaseEntity(taskId = task2.id, title = "Sukha Kriya preparatory breath", orderIndex = 0, targetMinutes = 7, isCompleted = false),
                TaskPhaseEntity(taskId = task2.id, title = "AUM meditation chant resonance", orderIndex = 1, targetMinutes = 8, isCompleted = false),
                TaskPhaseEntity(taskId = task2.id, title = "Stillness in Vipassana observation", orderIndex = 2, targetMinutes = 10, isCompleted = false)
            )
        )

        // 3. Task 3: Health & Body Energy
        val task3 = TaskEntity(
            id = "seed-task-health",
            title = "Pranic Forest Walk & Sunshine Grounding",
            description = "30 minutes barefoot grounding on soil with deep breath awareness to absorb solar energy.",
            clarityScore = 88f,
            impactScore = 84f,
            urgencyScore = 65f,
            energyLevel = EnergyLevel.MEDIUM.name,
            estimatedMinutes = 30,
            actualMinutesSpent = 0,
            category = TaskCategory.HEALTH.name,
            status = TaskStatus.TODAY.name,
            computedRank = DeterministicRankingEngine.calculateTaskRank(84f, 65f, 88f, EnergyLevel.MEDIUM),
            aiRationale = "Reconnecting with nature re-tunes the nervous system to cosmic rhythms."
        )
        taskDao.insertTask(task3)
        phaseDao.insertPhases(
            listOf(
                TaskPhaseEntity(taskId = task3.id, title = "Forest trail entry and quiet pacing", orderIndex = 0, targetMinutes = 15, isCompleted = false),
                TaskPhaseEntity(taskId = task3.id, title = "Tree contemplation & deep spine stretches", orderIndex = 1, targetMinutes = 15, isCompleted = false)
            )
        )

        // 4. Task 4: Completed Task (in history)
        val task4 = TaskEntity(
            id = "seed-task-completed-1",
            title = "Declutter Physical Sanctuary & Light Camphor",
            description = "Clear desk space, light natural camphor, remove visual turbulence from workspace.",
            clarityScore = 95f,
            impactScore = 85f,
            urgencyScore = 90f,
            energyLevel = EnergyLevel.LOW.name,
            estimatedMinutes = 15,
            actualMinutesSpent = 16,
            category = TaskCategory.PERSONAL.name,
            status = TaskStatus.COMPLETED.name,
            computedRank = DeterministicRankingEngine.calculateTaskRank(85f, 90f, 95f, EnergyLevel.LOW),
            aiRationale = "Physical simplicity reflects inward clarity.",
            completedAt = now - 3 * 60 * 60 * 1000L
        )
        taskDao.insertTask(task4)
        phaseDao.insertPhases(
            listOf(
                TaskPhaseEntity(taskId = task4.id, title = "Clear desk surface", orderIndex = 0, targetMinutes = 5, isCompleted = true, completedAt = now - 3 * 60 * 60 * 1000L),
                TaskPhaseEntity(taskId = task4.id, title = "Clean screens and light fragrance", orderIndex = 1, targetMinutes = 10, isCompleted = true, completedAt = now - 3 * 60 * 60 * 1000L)
            )
        )
        noteDao.insertNote(
            TaskNoteEntity(
                taskId = task4.id,
                content = "Room feels open and sacred. The scent of natural sandalwood brings immense stillness.",
                reflectionMood = "Ecstatic",
                createdAt = now - 3 * 60 * 60 * 1000L
            )
        )

        // 5. Standalone cosmic reflection note
        noteDao.insertNote(
            TaskNoteEntity(
                taskId = null,
                content = "Cosmic Realization: Action done with expectation of result produces anxiety; action done as an ecstatic expression of life produces liberation.",
                reflectionMood = "Ecstatic",
                createdAt = now - 5 * 60 * 60 * 1000L
            )
        )

        // 6. Realistic 7-day Historical Life Growth snapshots
        val snapshots = listOf(
            LifeGrowthSnapshotEntity(
                timestamp = now - (6 * dayMillis),
                growthScore = 520,
                mindfulFocusScore = 140,
                executionHarmonyScore = 150,
                mentalClarityScore = 120,
                cosmicMomentumScore = 110,
                dayLabel = "Mon",
                eventNote = "Initial Brain Dump Transmutation"
            ),
            LifeGrowthSnapshotEntity(
                timestamp = now - (5 * dayMillis),
                growthScore = 585,
                mindfulFocusScore = 170,
                executionHarmonyScore = 165,
                mentalClarityScore = 135,
                cosmicMomentumScore = 115,
                dayLabel = "Tue",
                eventNote = "Phase Mastery in deep work"
            ),
            LifeGrowthSnapshotEntity(
                timestamp = now - (4 * dayMillis),
                growthScore = 640,
                mindfulFocusScore = 195,
                executionHarmonyScore = 185,
                mentalClarityScore = 140,
                cosmicMomentumScore = 120,
                dayLabel = "Wed",
                eventNote = "Consecrated daily action streak"
            ),
            LifeGrowthSnapshotEntity(
                timestamp = now - (3 * dayMillis),
                growthScore = 695,
                mindfulFocusScore = 220,
                executionHarmonyScore = 205,
                mentalClarityScore = 145,
                cosmicMomentumScore = 125,
                dayLabel = "Thu",
                eventNote = "Overcame interruptions peacefully"
            ),
            LifeGrowthSnapshotEntity(
                timestamp = now - (2 * dayMillis),
                growthScore = 745,
                mindfulFocusScore = 240,
                executionHarmonyScore = 225,
                mentalClarityScore = 150,
                cosmicMomentumScore = 130,
                dayLabel = "Fri",
                eventNote = "Deep harmony of execution & reflection"
            ),
            LifeGrowthSnapshotEntity(
                timestamp = now - (1 * dayMillis),
                growthScore = 790,
                mindfulFocusScore = 255,
                executionHarmonyScore = 240,
                mentalClarityScore = 155,
                cosmicMomentumScore = 140,
                dayLabel = "Sat",
                eventNote = "Spiritual grounding & clarity"
            ),
            LifeGrowthSnapshotEntity(
                timestamp = now,
                growthScore = 835,
                mindfulFocusScore = 270,
                executionHarmonyScore = 255,
                mentalClarityScore = 165,
                cosmicMomentumScore = 145,
                dayLabel = "Today",
                eventNote = "Ecstatic Cosmic Alignment"
            )
        )
        lifeGrowthDao.insertSnapshots(snapshots)
    }

    companion object {
        @Volatile
        private var INSTANCE: LifeSortRepository? = null

        fun getInstance(context: Context): LifeSortRepository {
            return INSTANCE ?: synchronized(this) {
                val db = LifeSortDatabase.getInstance(context)
                val supabase = SupabaseClient(context)
                val repo = LifeSortRepository(db, supabase)
                INSTANCE = repo
                repo
            }
        }
    }
}
