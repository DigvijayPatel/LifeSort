import re
with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'r') as f:
    text = f.read()

text = text.replace('    fun completeTaskDirectly\n    }\n        viewModelScope.launch {', '    fun completeTaskDirectly(taskId: String) {\n        viewModelScope.launch {')

with open('app/src/main/java/com/example/ui/viewmodel/LifeSortViewModel.kt', 'w') as f:
    f.write(text)
