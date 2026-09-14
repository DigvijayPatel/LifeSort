package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.exp
import kotlin.random.Random

data class Particle(
    val initialXRatio: Float,
    val initialYRatio: Float,
    val color: Color,
    val angle: Float,
    val speed: Float,
    val gravity: Float,
    val size: Float,
    val rotationSpeed: Float,
    val shape: Int
)

@Composable
fun ConfettiOverlay(modifier: Modifier = Modifier) {
    val particles = remember {
        List(200) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            Particle(
                initialXRatio = 0.5f,
                initialYRatio = 0.4f, // Explode from slightly above center
                color = listOf(
                    Color(0xFFE2E2E2), Color(0xFFFFB300), Color(0xFF00E676),
                    Color(0xFFE91E63), Color(0xFF00B0FF), Color(0xFF9C27B0),
                    Color(0xFFFF9800), Color(0xFF00BCD4), Color(0xFFFF5722)
                ).random(),
                angle = angle,
                speed = Random.nextFloat() * 2000f + 800f, // Initial burst speed
                gravity = Random.nextFloat() * 1500f + 1000f, // Falling speed
                size = Random.nextFloat() * 28f + 12f,
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                shape = Random.nextInt(3) // 0: rect, 1: circle, 2: star/magic
            )
        }
    }

    val timeAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        timeAnim.animateTo(
            targetValue = 4.2f, // matching the celebration delay
            animationSpec = tween(durationMillis = 4200, easing = LinearEasing)
        )
    }

    val time = timeAnim.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            // Exponential decay for burst distance so they smoothly slow down horizontally
            val burstDistance = p.speed * (1f - exp(-time * 2.5f))
            val currentX = (width * p.initialXRatio) + (cos(p.angle) * burstDistance)
            val currentY = (height * p.initialYRatio) + (sin(p.angle) * burstDistance) + (0.5f * p.gravity * time * time)

            val currentRotation = time * p.rotationSpeed

            // Only draw if within bounds (roughly)
            if (currentX in -100f..(width + 100f) && currentY in -100f..(height + 100f)) {
                // Add a slight fade out at the end of the life
                val alpha = if (time > 3.2f) maxOf(0f, 1f - (time - 3.2f)) else 1f
                val colorWithAlpha = p.color.copy(alpha = alpha)

                rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                    when (p.shape) {
                        0 -> { // Rectangle (Confetti strip)
                            drawRect(
                                color = colorWithAlpha,
                                topLeft = Offset(currentX - p.size / 2, currentY - p.size * 0.75f),
                                size = Size(p.size, p.size * 1.5f)
                            )
                        }
                        1 -> { // Circle (Dot confetti)
                            drawCircle(
                                color = colorWithAlpha,
                                radius = p.size / 2,
                                center = Offset(currentX, currentY)
                            )
                        }
                        2 -> { // Sparkle / Magic Star
                            val path = androidx.compose.ui.graphics.Path().apply {
                                val halfSize = p.size / 2f
                                moveTo(currentX, currentY - halfSize)
                                quadraticBezierTo(currentX, currentY, currentX + halfSize, currentY)
                                quadraticBezierTo(currentX, currentY, currentX, currentY + halfSize)
                                quadraticBezierTo(currentX, currentY, currentX - halfSize, currentY)
                                quadraticBezierTo(currentX, currentY, currentX, currentY - halfSize)
                                close()
                            }
                            drawPath(path = path, color = colorWithAlpha)
                        }
                    }
                }
            }
        }
    }
}
