import re

with open('app/src/main/java/com/example/ui/screens/ActionsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.animation.animateContentSize\nimport androidx.compose.foundation.ExperimentalFoundationApi")

# In LazyColumn, items(items = sortedTasks, key = { it.id }) { task ->
# we want to add modifier = Modifier.animateItem() to ActionBacklogCard
# ActionBacklogCard does not take a modifier by default in the usage on line 279, wait, it has a modifier parameter!
# Let's replace `ActionBacklogCard(` with `ActionBacklogCard(\nmodifier = Modifier.animateItem(),` inside the items block.
# Actually, animateItem() requires a newer Compose version. Let's use `animateItemPlacement()` which is ExperimentalFoundationApi.
# Let's just use `animateItem()` and if it fails to compile, fallback to `animateItemPlacement()`.

