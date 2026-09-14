import re

with open('app/src/main/java/com/example/ui/screens/ActiveTimerScreen.kt', 'r') as f:
    content = f.read()

if "animateContentSize" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.animation.animateContentSize")

content = content.replace("            Card(", "            Card(\n                modifier = Modifier.animateContentSize(),")
content = content.replace("                modifier = Modifier.animateContentSize(),\n                modifier = Modifier", "                modifier = Modifier.animateContentSize()")

with open('app/src/main/java/com/example/ui/screens/ActiveTimerScreen.kt', 'w') as f:
    f.write(content)
