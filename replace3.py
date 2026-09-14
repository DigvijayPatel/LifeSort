import sys
with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    lines = f.readlines()
with open('patch_overlay.kt', 'r') as f:
    patch_lines = f.readlines()
lines = lines[:285] + patch_lines + lines[289:]
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.writelines(lines)
