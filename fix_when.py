import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# I will find "                    else -> {\n                        viewModel.navigateTo(Screen.Home)\n                    }\n                }"
# and replace the last "                }" with "                }\n                }"
target = """                    else -> {
                        viewModel.navigateTo(Screen.Home)
                    }
                }"""

replacement = """                    else -> {
                        viewModel.navigateTo(Screen.Home)
                    }
                }
                }"""

content = content.replace(target, replacement)
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
