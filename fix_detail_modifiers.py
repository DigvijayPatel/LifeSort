import re

with open('app/src/main/java/com/example/ui/screens/TaskDetailScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("                modifier = Modifier.animateContentSize(),\n                modifier = Modifier", "                modifier = Modifier.animateContentSize()")

with open('app/src/main/java/com/example/ui/screens/TaskDetailScreen.kt', 'w') as f:
    f.write(content)
