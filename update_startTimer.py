import re

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'r') as f:
    content = f.read()

start_replacement = """    fun startTimer(taskId: String, initialMinutes: Int = 25) {
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
    }"""

content = re.sub(r'    fun startTimer\(taskId: String, initialMinutes: Int = 25\) \{.*?navigateTo\(Screen.ActiveTimer\(taskId\)\)\s*\}\s*\}', start_replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'w') as f:
    f.write(content)
