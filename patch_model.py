import re

with open('app/src/main/java/com/example/data/model/LifeSortModels.kt', 'r') as f:
    content = f.read()

content = content.replace('val completedAt: Long? = null,', 'val completedAt: Long? = null,\n    val scheduledAt: Long? = null,')

with open('app/src/main/java/com/example/data/model/LifeSortModels.kt', 'w') as f:
    f.write(content)
