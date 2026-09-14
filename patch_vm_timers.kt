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
