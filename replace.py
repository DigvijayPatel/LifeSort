import sys
with open('app/src/main/java/com/example/ui/screens/ActionsScreen.kt', 'r') as f:
    lines = f.readlines()
with open('patch.kt', 'r') as f:
    patch_lines = f.readlines()
lines = lines[:119] + patch_lines + lines[154:]
with open('app/src/main/java/com/example/ui/screens/ActionsScreen.kt', 'w') as f:
    f.writelines(lines)
