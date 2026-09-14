import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import_statement = """import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
"""

if "AnimatedContent" not in content:
    content = content.replace("import android.os.Bundle", import_statement + "import android.os.Bundle")

    when_block = """                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {"""

    content = content.replace("                when (val screen = currentScreen) {", when_block)
    
    target = """                    else -> {
                        viewModel.navigateTo(Screen.Home)
                    }
                }
                   
                // Show celebration overlay"""

    replacement = """                    else -> {
                        viewModel.navigateTo(Screen.Home)
                    }
                }
                }
                   
                // Show celebration overlay"""
    
    content = content.replace(target, replacement)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
