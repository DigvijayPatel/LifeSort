import sys
with open('app/src/main/java/com/example/ui/screens/ActiveTimerScreen.kt', 'r') as f:
    lines = f.readlines()
with open('patch_companion.kt', 'r') as f:
    patch_lines = f.readlines()
lines = lines[:280] + patch_lines + lines[289:]
with open('app/src/main/java/com/example/ui/screens/ActiveTimerScreen.kt', 'w') as f:
    f.writelines(lines)
