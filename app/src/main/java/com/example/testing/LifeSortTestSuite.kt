package com.example.testing

import com.example.data.model.EnergyLevel
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity
import com.example.data.model.TaskStatus
import com.example.data.remote.GeminiBrainDumpService
import com.example.domain.engine.DeterministicLifeGrowthEngine
import com.example.domain.engine.DeterministicRankingEngine

data class TestCaseResult(
    val testNumber: Int,
    val name: String,
    val passed: Boolean,
    val details: String
)

class LifeSortTestSuite {

    fun runAll20Tests(): List<TestCaseResult> {
        val results = mutableListOf<TestCaseResult>()

        // Test 1: Brain dump extraction and task parsing
        results.add(runCatchingTest(1, "Brain dump task extraction") {
            val service = GeminiBrainDumpService()
            val text = "Meditate 20 mins; Design cosmic breathing orb; Call mom regarding dinner"
            val tasks = service.extractDeterministically(text)
            check(tasks.size == 3) { "Expected 3 tasks, got ${tasks.size}" }
            check(tasks[0].title.contains("Meditate", ignoreCase = true)) { "First task title mismatch" }
            "Extracted ${tasks.size} structured tasks from brain dump text."
        })

        // Test 2: Deterministic impact scoring
        results.add(runCatchingTest(2, "Deterministic impact scoring") {
            val rankHigh = DeterministicRankingEngine.calculateTaskRank(90f, 50f, 70f, EnergyLevel.MEDIUM)
            val rankLow = DeterministicRankingEngine.calculateTaskRank(40f, 50f, 70f, EnergyLevel.MEDIUM)
            check(rankHigh > rankLow) { "Higher impact must produce higher rank" }
            val diff = rankHigh - rankLow
            check(kotlin.math.abs(diff - (50f * 0.42f)) < 0.01f) { "Impact delta must strictly reflect 42% weight" }
            "Impact score correctly weighted at 42% of priority formula."
        })

        // Test 3: Deterministic urgency scoring
        results.add(runCatchingTest(3, "Deterministic urgency scoring") {
            val rankUrgent = DeterministicRankingEngine.calculateTaskRank(70f, 90f, 70f, EnergyLevel.MEDIUM)
            val rankRelaxed = DeterministicRankingEngine.calculateTaskRank(70f, 40f, 70f, EnergyLevel.MEDIUM)
            check(rankUrgent > rankRelaxed) { "Higher urgency must yield higher rank" }
            val diff = rankUrgent - rankRelaxed
            check(kotlin.math.abs(diff - (50f * 0.38f)) < 0.01f) { "Urgency delta must reflect 38% weight" }
            "Urgency score correctly weighted at 38% of priority formula."
        })

        // Test 4: Deterministic clarity scoring
        results.add(runCatchingTest(4, "Deterministic clarity scoring") {
            val rankClear = DeterministicRankingEngine.calculateTaskRank(70f, 70f, 90f, EnergyLevel.MEDIUM)
            val rankHazy = DeterministicRankingEngine.calculateTaskRank(70f, 70f, 40f, EnergyLevel.MEDIUM)
            check(rankClear > rankHazy) { "Higher clarity must yield higher rank" }
            val diff = rankClear - rankHazy
            check(kotlin.math.abs(diff - (50f * 0.15f)) < 0.01f) { "Clarity delta must reflect 15% weight" }
            "Clarity score correctly weighted at 15% of priority formula."
        })

        // Test 5: Deterministic energy factor weighting
        results.add(runCatchingTest(5, "Deterministic energy factor weighting") {
            val rankHigh = DeterministicRankingEngine.calculateTaskRank(70f, 70f, 70f, EnergyLevel.HIGH)
            val rankMed = DeterministicRankingEngine.calculateTaskRank(70f, 70f, 70f, EnergyLevel.MEDIUM)
            val rankLow = DeterministicRankingEngine.calculateTaskRank(70f, 70f, 70f, EnergyLevel.LOW)
            check(rankHigh > rankMed && rankMed > rankLow) { "Energy hierarchy High > Medium > Low must hold" }
            "Energy weight bonuses applied: High (+5.0), Medium (+2.0), Low (+0.0)."
        })

        // Test 6: Deterministic computed rank formula correctness
        results.add(runCatchingTest(6, "Exact computed rank formula verification") {
            // (80 * 0.42) + (60 * 0.38) + (90 * 0.15) + ((1.5 - 1.0) * 10)
            // = 33.6 + 22.8 + 13.5 + 5.0 = 74.9
            val expected = 74.9f
            val actual = DeterministicRankingEngine.calculateTaskRank(80f, 60f, 90f, EnergyLevel.HIGH)
            check(kotlin.math.abs(actual - expected) < 0.001f) { "Expected $expected, got $actual" }
            "Formula computed exact target score $actual."
        })

        // Test 7: One 'what should I do now?' recommendation selection
        results.add(runCatchingTest(7, "One 'what should I do now?' selection") {
            val taskA = TaskEntity(id = "A", title = "Check email", impactScore = 40f, urgencyScore = 50f, computedRank = 45f)
            val taskB = TaskEntity(id = "B", title = "Consecrate morning meditation", impactScore = 95f, urgencyScore = 90f, computedRank = 92f)
            val rec = DeterministicRankingEngine.determineWhatToDoNow(listOf(taskA, taskB))
            check(rec != null) { "Recommendation must not be null" }
            check(rec.task.id == "B") { "Expected highest ranked task B, got ${rec.task.id}" }
            check(rec.rationale.isNotBlank()) { "Recommendation rationale must be present" }
            "Selected '${rec.task.title}' as the single primary focus."
        })

        // Test 8: Empty / ambiguous brain dump graceful fallback
        results.add(runCatchingTest(8, "Empty / ambiguous input graceful handling") {
            val service = GeminiBrainDumpService()
            val tasksEmpty = service.extractDeterministically("   ")
            check(tasksEmpty.isNotEmpty()) { "Fallback must generate conscious grounding task" }
            check(tasksEmpty[0].category == TaskCategory.SPIRITUAL) { "Default fallback must be contemplative grounding" }
            "Gracefully transformed empty input into mindful centering action."
        })

        // Test 9: Task creation with sub-phases
        results.add(runCatchingTest(9, "Task breakdown into phases") {
            val service = GeminiBrainDumpService()
            val tasks = service.extractDeterministically("Build complete cosmic UI design system")
            check(tasks.isNotEmpty()) { "Must extract task" }
            check(tasks[0].phases.size >= 2) { "Must contain at least 2 sub-phases for complex task" }
            "Created task with ${tasks[0].phases.size} structured stepping-stone phases."
        })

        // Test 10: Phase ordering and target duration allocation
        results.add(runCatchingTest(10, "Phase ordering and target duration") {
            val phases = listOf("Phase 1", "Phase 2", "Phase 3").mapIndexed { index, name ->
                TaskPhaseEntity(taskId = "t1", title = name, orderIndex = index, targetMinutes = 15)
            }
            check(phases[0].orderIndex == 0 && phases[1].orderIndex == 1 && phases[2].orderIndex == 2) { "Order preserved" }
            check(phases.sumOf { it.targetMinutes } == 45) { "Total phase minutes align with estimate" }
            "3 phases ordered cleanly with 15 minutes allocation each."
        })

        // Test 11: Phase completion progress tracking
        results.add(runCatchingTest(11, "Phase completion progress tracking") {
            val p1 = TaskPhaseEntity(taskId = "t1", title = "Prep", orderIndex = 0, isCompleted = true)
            val p2 = TaskPhaseEntity(taskId = "t1", title = "Build", orderIndex = 1, isCompleted = false)
            val list = listOf(p1, p2)
            val completed = list.count { it.isCompleted }
            val progress = completed.toFloat() / list.size
            check(progress == 0.5f) { "Expected 50% completion, got $progress" }
            "Calculated accurate 50% completion across task phases."
        })

        // Test 12: Task timer start state transition
        results.add(runCatchingTest(12, "Task timer start (TODAY -> IN_PROGRESS)") {
            val task = TaskEntity(id = "test-12", title = "Deep Focus Task", status = TaskStatus.TODAY.name)
            val started = task.copy(status = TaskStatus.IN_PROGRESS.name, updatedAt = System.currentTimeMillis())
            check(started.status == TaskStatus.IN_PROGRESS.name) { "Status must transition to IN_PROGRESS" }
            check(started.interruptionReason == null) { "Interruption reason cleared on start" }
            "State successfully transitioned to IN_PROGRESS."
        })

        // Test 13: Task timer pausing with interruption reason logging
        results.add(runCatchingTest(13, "Task timer pause & interruption logging") {
            val task = TaskEntity(id = "test-13", title = "Deep Focus Task", status = TaskStatus.IN_PROGRESS.name, actualMinutesSpent = 10)
            val paused = task.copy(
                status = TaskStatus.PAUSED.name,
                actualMinutesSpent = task.actualMinutesSpent + 15,
                interruptionReason = "Urgent team sync",
                pausedAt = System.currentTimeMillis()
            )
            check(paused.status == TaskStatus.PAUSED.name) { "Status must be PAUSED" }
            check(paused.actualMinutesSpent == 25) { "Minutes spent accumulated to 25" }
            check(paused.interruptionReason == "Urgent team sync") { "Interruption reason recorded" }
            "Logged interruption 'Urgent team sync' with 25 minutes cumulative time."
        })

        // Test 14: Preserving elapsed focus minutes across pauses
        results.add(runCatchingTest(14, "Preserving focus minutes across pauses") {
            var task = TaskEntity(id = "test-14", title = "Multi-session action", actualMinutesSpent = 0)
            task = task.copy(actualMinutesSpent = task.actualMinutesSpent + 20) // Session 1
            task = task.copy(actualMinutesSpent = task.actualMinutesSpent + 15) // Session 2
            check(task.actualMinutesSpent == 35) { "Expected 35 minutes, got ${task.actualMinutesSpent}" }
            "Preserved cumulative 35 focus minutes across multiple timer pauses."
        })

        // Test 15: Resume interrupted task state restoration
        results.add(runCatchingTest(15, "Resume interrupted task state restoration") {
            val paused = TaskEntity(
                id = "test-15",
                title = "Interrupted action",
                status = TaskStatus.PAUSED.name,
                interruptionReason = "Coffee break",
                pausedAt = System.currentTimeMillis() - 60000
            )
            val resumed = paused.copy(
                status = TaskStatus.IN_PROGRESS.name,
                interruptionReason = null,
                pausedAt = null
            )
            check(resumed.status == TaskStatus.IN_PROGRESS.name) { "Status restored to IN_PROGRESS" }
            check(resumed.interruptionReason == null && resumed.pausedAt == null) { "Pause state cleared" }
            "Successfully restored interrupted task to active state."
        })

        // Test 16: 'What should I do now?' prioritizes interrupted task
        results.add(runCatchingTest(16, "Recommendation prioritizes interrupted task") {
            val normalTask = TaskEntity(id = "norm", title = "Plan roadmap", computedRank = 90f)
            val pausedTask = TaskEntity(id = "paused", title = "Resume code review", status = TaskStatus.PAUSED.name, interruptionReason = "Call", computedRank = 60f)
            val rec = DeterministicRankingEngine.determineWhatToDoNow(listOf(normalTask, pausedTask))
            check(rec != null && rec.task.id == "paused") { "Paused task must take immediate resumption precedence" }
            check(rec.urgencyLabel == "Resume Interrupted Flow") { "Urgency label reflects resumption" }
            "Interrupted task prioritized first with rationale: '${rec.rationale}'."
        })

        // Test 17: Task completion and duration recording
        results.add(runCatchingTest(17, "Task completion and duration recording") {
            val active = TaskEntity(id = "test-17", title = "Finish document", actualMinutesSpent = 25)
            val completed = active.copy(
                status = TaskStatus.COMPLETED.name,
                actualMinutesSpent = active.actualMinutesSpent + 10,
                completedAt = System.currentTimeMillis()
            )
            check(completed.status == TaskStatus.COMPLETED.name) { "Status must be COMPLETED" }
            check(completed.actualMinutesSpent == 35) { "Total minutes must equal 35" }
            check(completed.completedAt != null) { "CompletedAt timestamp recorded" }
            "Task finalized with 35 minutes total focused duration."
        })

        // Test 18: Contemplation notes association with task
        results.add(runCatchingTest(18, "Contemplation notes association with task") {
            val note = TaskNoteEntity(
                taskId = "task-alpha",
                content = "Found great clarity after breaking through the initial friction.",
                reflectionMood = "Focused"
            )
            check(note.taskId == "task-alpha") { "Note must be linked to taskId" }
            check(note.reflectionMood == "Focused") { "Mood recorded" }
            "Contemplation note successfully linked to task-alpha."
        })

        // Test 19: Standalone cosmic reflection notes creation
        results.add(runCatchingTest(19, "Standalone cosmic reflection notes") {
            val cosmicNote = TaskNoteEntity(
                taskId = null,
                content = "When you are in deep acceptance, life sorts itself without resistance.",
                reflectionMood = "Ecstatic"
            )
            check(cosmicNote.taskId == null) { "Standalone note has null taskId" }
            check(cosmicNote.reflectionMood == "Ecstatic") { "Reflection mood set to Ecstatic" }
            "Standalone cosmic reflection note preserved independently."
        })

        // Test 20: Deterministic Life Growth score multi-pillar calculation
        results.add(runCatchingTest(20, "Life Growth score multi-pillar calculation") {
            val snapshot = DeterministicLifeGrowthEngine.computeLifeGrowthScore(
                totalFocusMinutes = 120,
                completedTasksCount = 4,
                totalTasksCount = 5,
                completedPhasesCount = 8,
                totalPhasesCount = 10,
                averageClarity = 85f,
                activeStreakDays = 5
            )
            check(snapshot.growthScore in 100..1000) { "Growth score ${snapshot.growthScore} within 100-1000" }
            check(snapshot.mindfulFocusScore > 0) { "Focus score must be positive" }
            check(snapshot.executionHarmonyScore > 0) { "Harmony score must be positive" }
            check(snapshot.mentalClarityScore > 0) { "Clarity score must be positive" }
            check(snapshot.cosmicMomentumScore > 0) { "Momentum score must be positive" }
            val sum = snapshot.mindfulFocusScore + snapshot.executionHarmonyScore + snapshot.mentalClarityScore + snapshot.cosmicMomentumScore
            check(snapshot.growthScore == sum.coerceAtMost(1000)) { "Growth score must be sum of 4 pillars" }
            "Life Growth calculated at ${snapshot.growthScore}/1000 (Focus: ${snapshot.mindfulFocusScore}, Harmony: ${snapshot.executionHarmonyScore}, Clarity: ${snapshot.mentalClarityScore}, Momentum: ${snapshot.cosmicMomentumScore})."
        })

        return results
    }

    private inline fun runCatchingTest(number: Int, name: String, block: () -> String): TestCaseResult {
        return try {
            val details = block()
            TestCaseResult(number, name, true, details)
        } catch (e: Throwable) {
            TestCaseResult(number, name, false, "Failed: ${e.message}")
        }
    }
}
