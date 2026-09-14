package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EnergyLevel
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity
import com.example.data.remote.ExtractedTaskDto
import com.example.data.remote.GeminiBrainDumpService
import com.example.data.repository.LifeSortRepository
import com.example.domain.engine.DeterministicLifeGrowthEngine
import com.example.domain.engine.DeterministicRankingEngine
import com.example.domain.engine.Recommendation
import com.example.domain.gamification.CompletionReward
import com.example.domain.gamification.LifeLevel
import com.example.domain.gamification.LifeMilestone
import com.example.domain.gamification.LifeRpgEngine
import com.example.testing.LifeSortTestSuite
import com.example.testing.TestCaseResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data object Actions : Screen()
    data object BrainDump : Screen()
    data class TaskDetail(val taskId: String) : Screen()
    data class PhasePlanner(val taskId: String) : Screen()
    data class ActiveTimer(val taskId: String) : Screen()
    data object Notes : Screen()
    data object History : Screen()
    data object LifeGrowth : Screen()
    data object Settings : Screen()
}

class LifeSortViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LifeSortRepository.getInstance(application)
    private val geminiService = GeminiBrainDumpService()
    private val testSuite = LifeSortTestSuite()

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Database Observables
    val activeTasks: StateFlow<List<TaskEntity>> = repository.activeTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val deletedTasks: StateFlow<List<TaskEntity>> = repository.deletedTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val completedTasks: StateFlow<List<TaskEntity>> = repository.completedTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPhases: StateFlow<List<TaskPhaseEntity>> = repository.allPhases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allNotes: StateFlow<List<TaskNoteEntity>> = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestPausedTask: StateFlow<TaskEntity?> = repository.latestPausedTask.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val activeRunningTask: StateFlow<TaskEntity?> = repository.activeRunningTask.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val growthHistory: StateFlow<List<LifeGrowthSnapshotEntity>> = repository.growthHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestSnapshot: StateFlow<LifeGrowthSnapshotEntity?> = repository.latestSnapshot.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Gamification & Progression (Miracle of Mind Life RPG)
    val activeStreakDays = MutableStateFlow(5)

    private val _lastCompletionReward = MutableStateFlow<CompletionReward?>(null)
    val lastCompletionReward: StateFlow<CompletionReward?> = _lastCompletionReward.asStateFlow()

    private var celebrationDismissJob: Job? = null

    fun dismissCelebration() {
        _lastCompletionReward.value = null
        celebrationDismissJob?.cancel()
    }

    private fun triggerCelebration(reward: CompletionReward) {
        _lastCompletionReward.value = reward
        celebrationDismissJob?.cancel()
        celebrationDismissJob = viewModelScope.launch {
            delay(4200L)
            _lastCompletionReward.value = null
        }
    }

    val currentRpgLevel: StateFlow<LifeLevel> = latestSnapshot.map { snapshot ->
        val score = snapshot?.growthScore ?: 780
        LifeRpgEngine.getLevelForScore(score)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LifeRpgEngine.getLevelForScore(780)
    )

    val progressToNextLevel: StateFlow<Float> = latestSnapshot.map { snapshot ->
        val score = snapshot?.growthScore ?: 780
        LifeRpgEngine.getProgressToNextLevel(score)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LifeRpgEngine.getProgressToNextLevel(780)
    )

    val unlockedMilestones: StateFlow<List<LifeMilestone>> = combine(
        completedTasks,
        activeStreakDays,
        latestSnapshot
    ) { completed, streak, snapshot ->
        val totalMins = completed.sumOf { it.actualMinutesSpent }
        val score = snapshot?.growthScore ?: 780
        LifeRpgEngine.evaluateMilestones(completed.size, totalMins, streak, score)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Derived: "What should I do now?" Recommendation
    val recommendation: StateFlow<Recommendation?> = combine(activeTasks, allPhases) { tasks, phases ->
        val phasesMap = phases.groupBy { it.taskId }
        DeterministicRankingEngine.determineWhatToDoNow(tasks, phasesMap)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Brain Dump State
    private val _brainDumpInput = MutableStateFlow("")
    val brainDumpInput: StateFlow<String> = _brainDumpInput.asStateFlow()

    private val _isAnalyzingBrainDump = MutableStateFlow(false)
    val isAnalyzingBrainDump: StateFlow<Boolean> = _isAnalyzingBrainDump.asStateFlow()

    private val _extractedTasks = MutableStateFlow<List<ExtractedTaskDto>>(emptyList())
    val extractedTasks: StateFlow<List<ExtractedTaskDto>> = _extractedTasks.asStateFlow()
    private val _companionMessage = MutableStateFlow<String?>(null)
    val companionMessage: StateFlow<String?> = _companionMessage.asStateFlow()

    fun refreshCompanionMessage(taskId: String) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: return@launch
            cancelTimerWarning(taskId)
            cancelPausedReminder(taskId)
            val phases = repository.allPhases.first().filter { it.taskId == taskId && !it.isCompleted }.map { it.title }
            val notes = repository.allNotes.first().filter { it.taskId == taskId }.map { it.content }
            _companionMessage.value = "Tuning into your frequency..."
            val message = geminiService.generateCompanionCheckIn(task.title, phases, notes)
            _companionMessage.value = message
        }
    }

    fun updateBrainDumpInput(text: String) {
        _brainDumpInput.value = text
    }


    fun instantAddIntention() {
        val text = _brainDumpInput.value
        if (text.isBlank()) return
        
        viewModelScope.launch {
            val dto = ExtractedTaskDto(
                title = text,
                description = "",
                clarityScore = 100f,
                impactScore = 50f,
                urgencyScore = 50f,
                energyLevel = EnergyLevel.MEDIUM,
                estimatedMinutes = 15,
                category = TaskCategory.PERSONAL,
                phases = emptyList(),
                aiRationale = "Instant user intention.",
                computedRank = 100f
            )
            repository.saveExtractedTasks(listOf(dto))
            _brainDumpInput.value = ""
            navigateTo(Screen.Home)
        }
    }

    fun analyzeBrainDump() {
        val text = _brainDumpInput.value
        if (text.isBlank()) return

        viewModelScope.launch {
            _isAnalyzingBrainDump.value = true
            val extracted = geminiService.extractTasksFromBrainDump(text)
            _extractedTasks.value = extracted
            _isAnalyzingBrainDump.value = false
        }
    }

    fun saveExtractedTasksToSanctuary() {
        val list = _extractedTasks.value
        if (list.isEmpty()) return

        viewModelScope.launch {
            repository.saveExtractedTasks(list)
            _extractedTasks.value = emptyList()
            _brainDumpInput.value = ""
            navigateTo(Screen.Home)
        }
    }

    fun clearExtractedTasks() {
        _extractedTasks.value = emptyList()
    }

    // Active Focus Timer State
    private val _timerRemainingSeconds = MutableStateFlow(25 * 60)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerElapsedSeconds = MutableStateFlow(0)
    val timerElapsedSeconds: StateFlow<Int> = _timerElapsedSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(taskId: String, initialMinutes: Int = 25) {
        viewModelScope.launch {
            repository.startTaskTimer(taskId)
            refreshCompanionMessage(taskId)
            _timerRemainingSeconds.value = initialMinutes * 60
            _timerElapsedSeconds.value = 0
            _isTimerRunning.value = true

            val task = repository.getTaskById(taskId)
            if (task != null) {
                cancelPausedReminder(taskId)
                scheduleTimerWarningIfApplicable(taskId, task.title)
            }

            startTimerTicker()
            navigateTo(Screen.ActiveTimer(taskId))
        }
    }

    private fun startTimerTicker() {

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000L)
                if (_isTimerRunning.value) {
                    _timerElapsedSeconds.value += 1
                    if (_timerRemainingSeconds.value > 0) {
                        _timerRemainingSeconds.value -= 1
                    }
                }
            }
        }
    }

    fun pauseTimer(taskId: String, reason: String) {
        viewModelScope.launch {
            _isTimerRunning.value = false
            timerJob?.cancel()
            val minutesSpent = (_timerElapsedSeconds.value / 60).coerceAtLeast(1)
            repository.pauseTaskTimer(taskId, minutesSpent, reason)
            val task = repository.getTaskById(taskId)
            if (task != null) {
                cancelTimerWarning(taskId)
                schedulePausedReminder(taskId, task.title)
            }
        }
    }

    fun resumeTimer(taskId: String) {
        viewModelScope.launch {
            repository.resumeTaskTimer(taskId)
            refreshCompanionMessage(taskId)
            _isTimerRunning.value = true
            val task = repository.getTaskById(taskId)
            if (task != null) {
                cancelPausedReminder(taskId)
                scheduleTimerWarningIfApplicable(taskId, task.title)
            }
            startTimerTicker()
            navigateTo(Screen.ActiveTimer(taskId))
        }
    }

    fun completeTaskFromTimer(taskId: String, reflection: String?) {
        viewModelScope.launch {
            cancelTimerWarning(taskId)
            cancelPausedReminder(taskId)
            _isTimerRunning.value = false
            timerJob?.cancel()
            val minutesSpent = (_timerElapsedSeconds.value / 60).coerceAtLeast(1)
            val task = repository.getTaskById(taskId)
            val prevScore = latestSnapshot.value?.growthScore ?: 780
            val prevCompleted = completedTasks.value.size
            val prevMins = completedTasks.value.sumOf { it.actualMinutesSpent }
            val streak = activeStreakDays.value

            repository.completeTask(taskId, minutesSpent, reflection)

            if (task != null) {
                val reward = LifeRpgEngine.computeReward(
                    task = task,
                    previousScore = prevScore,
                    minutesSpent = minutesSpent,
                    currentCompletedCount = prevCompleted,
                    activeStreakDays = streak,
                    totalFocusMinutes = prevMins
                )
                triggerCelebration(reward)
            }
            navigateTo(Screen.Home)
        }
    }

    // Tasks & Phases Management
    fun togglePhase(phaseId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.togglePhase(phaseId, isCompleted)
        }
    }

    fun addPhase(taskId: String, title: String, targetMinutes: Int) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addPhaseToTask(taskId, title, targetMinutes)
        }
    }

    fun addNote(taskId: String?, content: String, mood: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.addNote(taskId, content, mood)
        }
    }

    fun restoreTask(taskId: String) {
        viewModelScope.launch {
            repository.restoreTask(taskId)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun scheduleTask(taskId: String, scheduledTime: Long) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                com.example.NotificationHelper.scheduleTaskNotification(getApplication(), taskId, task.title, scheduledTime)
            }
        }
    }

    fun completeTaskDirectly(taskId: String) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: return@launch
            cancelTimerWarning(taskId)
            cancelPausedReminder(taskId)
            val minutes = task.estimatedMinutes
            val prevScore = latestSnapshot.value?.growthScore ?: 780
            val prevCompleted = completedTasks.value.size
            val prevMins = completedTasks.value.sumOf { it.actualMinutesSpent }
            val streak = activeStreakDays.value

            repository.completeTask(taskId, minutes, "Completed in conscious flow")

            val reward = LifeRpgEngine.computeReward(
                task = task,
                previousScore = prevScore,
                minutesSpent = minutes,
                currentCompletedCount = prevCompleted,
                activeStreakDays = streak,
                totalFocusMinutes = prevMins
            )
            triggerCelebration(reward)
        }
    }

    // Supabase Cloud Sync & Configuration
    val supabaseUrl = repository.supabaseClient.supabaseUrl
    val supabaseKey = repository.supabaseClient.supabaseAnonKey

    private val _supabaseStatus = MutableStateFlow<String>("Offline Sanctuary Mode")
    val supabaseStatus: StateFlow<String> = _supabaseStatus.asStateFlow()

    fun updateSupabaseConfig(url: String, key: String) {
        repository.supabaseClient.supabaseUrl = url
        repository.supabaseClient.supabaseAnonKey = key
        testSupabaseConnection()
    }

    fun testSupabaseConnection() {
        viewModelScope.launch {
            _supabaseStatus.value = "Testing connection to Supabase..."
            val (ok, message) = repository.supabaseClient.testConnection()
            _supabaseStatus.value = if (ok) "Connected: $message" else "Error: $message"
        }
    }

    // Seed Data Reload
    fun reloadSeedData() {
        viewModelScope.launch {
            repository.seedRealisticCosmicData()
        }
    }

    // 20 End-to-End Tests Runner
    private val _testResults = MutableStateFlow<List<TestCaseResult>>(emptyList())
    val testResults: StateFlow<List<TestCaseResult>> = _testResults.asStateFlow()

    private val _isRunningTests = MutableStateFlow(false)
    val isRunningTests: StateFlow<Boolean> = _isRunningTests.asStateFlow()

    fun run20E2ETests() {
        viewModelScope.launch {
            _isRunningTests.value = true
            delay(300L) // UI feedback
            val results = testSuite.runAll20Tests()
            _testResults.value = results
            _isRunningTests.value = false
        }
    }

    init {
        // Automatically check if database is empty on first boot, and seed realistic data
        viewModelScope.launch {
            val existing = repository.allTasks.first()
            val snaps = repository.growthHistory.first()
            if (existing.isEmpty() && snaps.isEmpty()) {
                repository.seedRealisticCosmicData()
            }
        }
    }
    private fun scheduleTimerWarningIfApplicable(taskId: String, taskTitle: String) {
        val remaining = _timerRemainingSeconds.value
        if (remaining > 300) { // More than 5 mins
            val triggerTime = System.currentTimeMillis() + ((remaining - 300) * 1000L)
            com.example.NotificationHelper.scheduleTimer5MinWarning(getApplication(), taskId, taskTitle, triggerTime)
        }
    }
    
    private fun cancelTimerWarning(taskId: String) {
        com.example.NotificationHelper.cancelTimer5MinWarning(getApplication(), taskId)
    }

    private fun schedulePausedReminder(taskId: String, taskTitle: String) {
        // Remind in 15 minutes
        val triggerTime = System.currentTimeMillis() + (15 * 60 * 1000L)
        com.example.NotificationHelper.schedulePausedTaskReminder(getApplication(), taskId, taskTitle, triggerTime)
    }

    private fun cancelPausedReminder(taskId: String) {
        com.example.NotificationHelper.cancelPausedTaskReminder(getApplication(), taskId)
    }
}
