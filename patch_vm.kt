    private val _companionMessage = MutableStateFlow<String?>(null)
    val companionMessage = _companionMessage.asStateFlow()

    fun refreshCompanionMessage(taskId: String) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: return@launch
            val phases = repository.allPhases.first().filter { it.taskId == taskId && !it.isCompleted }.map { it.title }
            val notes = repository.allNotes.first().filter { it.taskId == taskId }.map { it.content }
            _companionMessage.value = "Tuning into your frequency..."
            val message = geminiService.generateCompanionCheckIn(task.title, phases, notes)
            _companionMessage.value = message
        }
    }
