import re

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'r') as f:
    content = f.read()

pause_replacement = """    fun pauseTimer(taskId: String, reason: String) {
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
    }"""

resume_replacement = """    fun resumeTimer(taskId: String) {
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
    }"""

content = re.sub(r'    fun pauseTimer\(taskId: String, reason: String\) \{.*?\}\s*\}', pause_replacement, content, flags=re.DOTALL)
content = re.sub(r'    fun resumeTimer\(taskId: String\) \{.*?navigateTo\(Screen.ActiveTimer\(taskId\)\)\s*\}\s*\}', resume_replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'w') as f:
    f.write(content)
