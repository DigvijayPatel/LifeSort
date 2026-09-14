import re

with open('app/src/main/java/com/example/ui/screens/ActionsScreen.kt', 'r') as f:
    content = f.read()

# I want to add `Modifier.animateItem()` to the items in LazyColumn.
# But animateItem() is in Compose 1.7. Let's use `Modifier.animateItemPlacement()` if not available, but `animateItem()` is safer if available, or just `animateContentSize()` on the Card.
# Let's add animateContentSize to CosmicCard in ActionsScreen.
content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.animation.animateContentSize")

# Find CosmicCard usages in ActionsScreen and add animateContentSize
content = content.replace("                    CosmicCard(", "                    CosmicCard(\n                        modifier = Modifier.animateContentSize(),")

# Actually CosmicCard might already take a modifier. Let's check how it's used.
