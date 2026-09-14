with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    text = f.read()

import re
matches = list(re.finditer(r'\{|\}', text))
depth = 0
for m in matches:
    if m.group(0) == '{': depth += 1
    else: depth -= 1

print("Final depth:", depth)
