import re

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'r') as f:
    content = f.read()

# I will extract the 4 helper functions out of startTimerTicker and put them at the end of the class.
helpers = """    private fun scheduleTimerWarningIfApplicable(taskId: String, taskTitle: String) {
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
    }"""

# Remove the broken lines from inside startTimerTicker
content = re.sub(r'    private fun scheduleTimerWarningIfApplicable.*?cancelPausedTaskReminder\(getApplication\(\), taskId\)\s*\}', '', content, flags=re.DOTALL)

# Insert the helpers at the end of the class (before the last closing brace)
content = content.rstrip()
if content.endswith('}'):
    content = content[:-1] + helpers + "\n}\n"

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'w') as f:
    f.write(content)
