import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

target = """                if (showBottomBar) {
                    CosmicBottomNavigationBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }"""

replacement = """                androidx.compose.animation.AnimatedVisibility(
                    visible = showBottomBar,
                    enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) + androidx.compose.animation.fadeOut()
                ) {
                    CosmicBottomNavigationBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }"""

content = content.replace(target, replacement)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
