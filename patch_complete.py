import re

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'r') as f:
    content = f.read()

complete_replacement = """    fun completeTaskFromTimer(taskId: String, reflection: String?) {
        viewModelScope.launch {
            cancelTimerWarning(taskId)
            cancelPausedReminder(taskId)
            _isTimerRunning.value = false
            timerJob?.cancel()
            val minutesSpent = (_timerElapsedSeconds.value / 60).coerceAtLeast(1)"""

content = re.sub(r'    fun completeTaskFromTimer\(taskId: String, reflection: String\?\) \{\s*viewModelScope.launch \{\s*_isTimerRunning.value = false\s*timerJob\?\.cancel\(\)\s*val minutesSpent = \(_timerElapsedSeconds\.value / 60\)\.coerceAtLeast\(1\)', complete_replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'w') as f:
    f.write(content)
