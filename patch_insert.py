with open('app/src/main/java/com/example/ui/screens/TaskDetailScreen.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "if (task.status != \"COMPLETED\") {" in line:
        insert_idx = i + 1
        break

with open('patch_schedule_btn.kt', 'r') as f:
    patch = f.readlines()

lines = lines[:insert_idx] + patch + lines[insert_idx:]

with open('app/src/main/java/com/example/ui/screens/TaskDetailScreen.kt', 'w') as f:
    f.writelines(lines)
