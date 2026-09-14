import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

import_statement = """import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
"""

# add imports
content = content.replace("import android.os.Bundle", import_statement + "import android.os.Bundle")

# replace when
when_block = """                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {"""

content = content.replace("                when (val screen = currentScreen) {", when_block)

# The when block ends at line 286. Wait, I should just use regex or find the end of when.
