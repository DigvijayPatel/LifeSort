import re

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'r') as f:
    content = f.read()

replacement = """    fun scheduleTask(taskId: String, scheduledTime: Long) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                com.example.NotificationHelper.scheduleTaskNotification(getApplication(), taskId, task.title, scheduledTime)
            }
        }
    }

    fun completeTaskDirectly"""

content = re.sub(r'    fun completeTaskDirectly.*?fun scheduleTask.*?\}\s*\}', replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'w') as f:
    f.write(content)
