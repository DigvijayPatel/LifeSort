import re

with open('app/src/main/java/com/example/ui/screens/ActionsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.animation.animateContentSize\nimport androidx.compose.foundation.ExperimentalFoundationApi")

# In ActionBacklogCard definition:
content = content.replace("        modifier = modifier\n            .fillMaxWidth()", "        modifier = modifier\n            .fillMaxWidth()\n            .animateContentSize()")

# In items(items = sortedTasks, key = { it.id })
# Let's see how it's called
#                                ActionBacklogCard(
#                                    task = task,
#                                    phases = taskPhases,
content = content.replace("                            ActionBacklogCard(\n                                task = task,", "                            ActionBacklogCard(\n                                modifier = Modifier.animateItem(),\n                                task = task,")

with open('app/src/main/java/com/example/ui/screens/ActionsScreen.kt', 'w') as f:
    f.write(content)
