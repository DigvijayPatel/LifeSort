with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    text = f.read()

import re
matches = list(re.finditer(r'\{|\}', text))
depth = 0
for i, m in enumerate(matches):
    if m.group(0) == '{': depth += 1
    else: depth -= 1
    if depth < 0:
        print(f"Negative depth at match {i} around: {text[max(0, m.start()-20):m.end()+20]}")

print("Final depth:", depth)
