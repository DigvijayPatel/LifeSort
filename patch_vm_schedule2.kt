    fun scheduleTask(taskId: String, scheduledTime: Long) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                com.example.NotificationHelper.scheduleTaskNotification(getApplication(), taskId, task.title, scheduledTime)
            }
        }
    }
