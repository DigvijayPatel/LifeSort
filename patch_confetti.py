import re

with open('app/src/main/java/com/example/ui/components/ConfettiOverlay.kt', 'r') as f:
    content = f.read()

content = content.replace("import kotlin.math.sin", "import kotlin.math.sin\nimport kotlin.math.exp")

bad_math = """            // Drag coefficient to slow down the initial burst
            val drag = maxOf(0f, 1f - time * 0.9f)
            
            val burstDistance = p.speed * time * drag"""

good_math = """            // Exponential decay for burst distance so they smoothly slow down horizontally
            val burstDistance = p.speed * (1f - exp(-time * 2.5f))"""

content = content.replace(bad_math, good_math)

with open('app/src/main/java/com/example/ui/components/ConfettiOverlay.kt', 'w') as f:
    f.write(content)
