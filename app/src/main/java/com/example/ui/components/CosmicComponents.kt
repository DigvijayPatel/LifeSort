package com.example.ui.components
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.RepeatMode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskEntity
import com.example.domain.engine.Recommendation
import com.example.ui.theme.AuraTeal
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBlueGlow
import com.example.ui.theme.CosmicBluePrimary
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSoftBlue
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.EnergyHigh
import com.example.ui.theme.EnergyLow
import com.example.ui.theme.EnergyMedium
import com.example.ui.theme.EtherealPurple
import com.example.ui.theme.MangoDeep
import com.example.ui.theme.MangoMuted
import com.example.ui.theme.MangoSoft
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.MysticCyan
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.StarlightGold
import com.example.ui.theme.SunburstWarm
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable

import androidx.compose.animation.core.animateFloat

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Draw some static stars
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starCount = 60
            for (i in 0 until starCount) {
                // Pseudo-random star positions based on index
                val randomX = ((i * 87) % 100) / 100f * size.width
                val randomY = ((i * 123) % 100) / 100f * size.height
                val randomAlpha = ((i * 47) % 100) / 100f * 0.7f + 0.1f
                val randomRadius = ((i * 13) % 100) / 100f * 2.5f + 1f
                drawCircle(
                    color = Color.White.copy(alpha = randomAlpha),
                    radius = randomRadius,
                    center = Offset(randomX, randomY)
                )
            }
        }
        content()
    }
}

/**
 * The Hero Breathing Luminous Orb
 * Inspired by the ecstatic, contemplative emotional feel of Sadhguru's Boom app.
 * A much softer, larger breathing luminous sphere with fluid moving cosmic glow,
 * rotating planetary rings, floating stars, and the Life Score centered with crystal clarity.
 */
@Composable
fun BreathingLuminousOrb(
    growthScore: Int,
    growthLevel: String = "Harmonic Vitality",
    levelNumber: Int = 3,
    isCelebration: Boolean = false,
    celebrationXp: Int? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbBreath")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbBreathScale"
    )

    // Celebration burst bounce
    val celebrationScale by animateFloatAsState(
        targetValue = if (isCelebration) 1.20f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CelebrationScale"
    )

    // Celebration shockwave expanding alpha
    val celebrationShockwaveAlpha by animateFloatAsState(
        targetValue = if (isCelebration) 0.85f else 0.0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "CelebrationShockwaveAlpha"
    )

    // Animated score transition
    val animatedScore by animateIntAsState(
        targetValue = growthScore,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "AnimatedScore"
    )

    // Format score gracefully: e.g. 784 -> "78.4" or score formatted cleanly
    val formattedScore = if (animatedScore >= 100) {
        String.format(Locale.US, "%.1f", animatedScore / 10.0f)
    } else {
        "$animatedScore"
    }

    val effectiveRings = levelNumber.coerceIn(1, 6)
    val effectiveParticles = (6 + levelNumber * 2).coerceIn(6, 20)

    Box(
        modifier = modifier
            .size(230.dp)
            .testTag("hero_breathing_orb")
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .graphicsLayer {
                scaleX = breathScale
                scaleY = breathScale
            },
        contentAlignment = Alignment.Center
    ) {
        // Simple static multi-layered celestial canvas to reduce CPU usage
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f * celebrationScale

            // 0. CELEBRATION SHOCKWAVE BURST
            if (celebrationShockwaveAlpha > 0.05f) {
                drawCircle(
                    color = MangoWarm.copy(alpha = 0.5f * celebrationShockwaveAlpha),
                    radius = baseRadius * 1.85f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 1. Soft cosmic blue halo (Faded Orbits)
            drawCircle(
                color = CosmicBlueGlow.copy(alpha = 0.45f),
                radius = baseRadius * 1.65f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 2. Secondary soft cyan-blue starlight bloom
            drawCircle(
                color = MysticCyan.copy(alpha = 0.35f),
                radius = baseRadius * 1.35f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // 3. Static center aura
            drawCircle(
                color = CosmicBluePrimary.copy(alpha = 0.25f),
                radius = baseRadius * 0.90f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. EVOLVING SACRED MANDALA RINGS (Grows with RPG Life Level 1..6)
            // Ring 1 (Available at Level 1+): Outer subtle elliptical ring
            drawOval(
                color = CosmicBluePrimary.copy(alpha = 0.30f),
                topLeft = Offset(center.x - baseRadius * 1.25f, center.y - baseRadius * 0.60f),
                size = androidx.compose.ui.geometry.Size(baseRadius * 2.50f, baseRadius * 1.20f),
                style = Stroke(width = 1.2f)
            )

            // Ring 2 (Level 2+): Inner tilted starlight ring
            if (effectiveRings >= 2) {
                rotate(degrees = 45f, pivot = center) {
                    drawOval(
                        color = MangoWarm.copy(alpha = 0.24f),
                        topLeft = Offset(center.x - baseRadius * 1.15f, center.y - baseRadius * 0.45f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 2.30f, baseRadius * 0.90f),
                        style = Stroke(width = 1.0f)
                    )
                }
            }

            // Ring 3 (Level 3+): Perpendicular resonant mandala ellipse
            if (effectiveRings >= 3) {
                rotate(degrees = 90f, pivot = center) {
                    drawOval(
                        color = CelestialViolet.copy(alpha = 0.22f),
                        topLeft = Offset(center.x - baseRadius * 1.20f, center.y - baseRadius * 0.52f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 2.40f, baseRadius * 1.04f),
                        style = Stroke(width = 1.1f)
                    )
                }
            }

            // 5. HIGH CONTRAST CENTER LENS: Soft, translucent cosmic backdrop for clear readability
            drawCircle(
                color = CosmicSurface.copy(alpha = 0.85f),
                radius = baseRadius * 0.78f,
                center = center
            )

            // 6. Ultra-fine starlight boundary ring
            drawCircle(
                color = CosmicBluePrimary.copy(alpha = 0.65f),
                radius = baseRadius * 0.74f,
                center = center,
                style = Stroke(width = 1.4f)
            )
        }

        // Text content centered precisely and legibly inside the breathing orb
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // If rewarding completion burst is active, celebrate!
            AnimatedVisibility(
                visible = isCelebration && celebrationXp != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MangoWarm,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "+$celebrationXp",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmicVoid,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            if (!isCelebration || celebrationXp == null) {
                Text(
                    text = "✦",
                    fontSize = 20.sp,
                    color = MangoWarm,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formattedScore,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    lineHeight = 48.sp
                ),
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = (-1.5).sp
            )
        }
    }
}

/**
 * Three Clean Vitality Metric Pills
 * Sleek, elegant cosmic summary cards (Clarity, Momentum, Life Value)
 */
@Composable
fun VitalityMetricsRow(
    clarityScore: String = "+4.2",
    momentumScore: String = "+3.8",
    lifeValueScore: String = "+6.8",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VitalityMetricPill(
            score = clarityScore,
            label = "CLARITY",
            accentColor = MysticCyan,
            modifier = Modifier.weight(1f)
        )
        VitalityMetricPill(
            score = momentumScore,
            label = "MOMENTUM",
            accentColor = CosmicBluePrimary,
            modifier = Modifier.weight(1f)
        )
        VitalityMetricPill(
            score = lifeValueScore,
            label = "LIFE VALUE",
            accentColor = MangoWarm,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun VitalityMetricPill(
    score: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.75f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = score,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = accentColor,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Historical Life Growth Graph
 * Displayed separately below the breathing orb: interactive, elegant, smooth cosmic curve.
 */
@Composable
fun HistoricalLifeGrowthGraph(
    history: List<LifeGrowthSnapshotEntity>,
    modifier: Modifier = Modifier,
    onPointSelected: (LifeGrowthSnapshotEntity) -> Unit = {}
) {
    if (history.isEmpty()) return

    var selectedIndex by remember { mutableStateOf(history.lastIndex) }
    val selectedSnapshot = history.getOrNull(selectedIndex) ?: history.last()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("historical_growth_graph"),
        colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.90f)),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIFE JOURNEY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Your growth over time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPure
                    )
                }

                Surface(
                    color = CosmicSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                ) {
                    val formattedSelected = if (selectedSnapshot.growthScore >= 100) {
                        String.format(Locale.US, "%.1f", selectedSnapshot.growthScore / 10.0f)
                    } else {
                        "${selectedSnapshot.growthScore}"
                    }
                    Text(
                        text = "$formattedSelected • ${selectedSnapshot.dayLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmicBluePrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (selectedSnapshot.eventNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“${selectedSnapshot.eventNote}”",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSerene,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Cosmic Canvas Curve
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val width = constraints.maxWidth.toFloat()
                val height = constraints.maxHeight.toFloat()
                val minScore = (history.minOfOrNull { it.growthScore } ?: 400).coerceAtLeast(100) - 40
                val maxScore = (history.maxOfOrNull { it.growthScore } ?: 900) + 40
                val scoreRange = (maxScore - minScore).coerceAtLeast(1).toFloat()

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(history) {
                            detectTapGestures { offset ->
                                val stepX = width / (history.size - 1).coerceAtLeast(1)
                                val index = ((offset.x + stepX / 2f) / stepX).toInt().coerceIn(0, history.lastIndex)
                                selectedIndex = index
                                onPointSelected(history[index])
                            }
                        }
                ) {
                    val points = history.mapIndexed { index, item ->
                        val x = if (history.size > 1) {
                            (index.toFloat() / (history.size - 1)) * (width - 44.dp.toPx()) + 22.dp.toPx()
                        } else {
                            width / 2f
                        }
                        val normalized = (item.growthScore - minScore) / scoreRange
                        val y = height - (normalized * (height - 34.dp.toPx())) - 17.dp.toPx()
                        Offset(x, y)
                    }

                    // Background gridlines in subtle cosmic starlight
                    for (i in 1..3) {
                        val gridY = (height / 4f) * i
                        drawLine(
                            color = CosmicBorder.copy(alpha = 0.40f),
                            start = Offset(0f, gridY),
                            end = Offset(width, gridY),
                            strokeWidth = 1f
                        )
                    }

                    if (points.size >= 2) {
                        // Smooth cubic bezier trajectory
                        val strokePath = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val controlX = (p0.x + p1.x) / 2f
                                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            }
                        }

                        // Gradient fill beneath the curve
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(points.last().x, height)
                            lineTo(points.first().x, height)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CosmicBluePrimary.copy(alpha = 0.28f),
                                    CelestialViolet.copy(alpha = 0.12f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = height
                            ),
                            style = Fill
                        )

                        // Smooth glowing curve stroke in cosmic gradient
                        drawPath(
                            path = strokePath,
                            brush = Brush.horizontalGradient(
                                colors = listOf(MysticCyan, CosmicBluePrimary, CelestialViolet)
                            ),
                            style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // Data point star nodes
                    points.forEachIndexed { i, pt ->
                        val isSelected = i == selectedIndex
                        val nodeRadius = if (isSelected) 6.dp.toPx() else 4.dp.toPx()
                        val color = if (isSelected) MangoWarm else CosmicBluePrimary

                        if (isSelected) {
                            // Pulsing halo for selected point
                            drawCircle(
                                color = MangoWarm.copy(alpha = 0.35f),
                                radius = 13.dp.toPx(),
                                center = pt
                            )
                        }

                        drawCircle(
                            color = color,
                            radius = nodeRadius,
                            center = pt
                        )

                        drawCircle(
                            color = CosmicVoid,
                            radius = nodeRadius * 0.45f,
                            center = pt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day labels below graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                history.forEachIndexed { i, item ->
                    Text(
                        text = item.dayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (i == selectedIndex) Color.White else TextMuted,
                        fontWeight = if (i == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * The Golden Luminous "What Should I Do Now?" Card
 * The single primary focus recommendation positioned directly below the hero breathing orb.
 */
@Composable
fun WhatToDoNowHeroCard(
    recommendation: Recommendation?,
    onStartFocus: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendation == null) return

    val task = recommendation.task

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("what_to_do_now_card"),
        colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(MangoWarm, CelestialViolet))
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MangoWarm.copy(alpha = 0.18f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Focus",
                            tint = MangoWarm,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WHAT TO DO NEXT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MangoWarm,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )
                }

                Surface(
                    color = CosmicCard,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                ) {
                    Text(
                        text = recommendation.urgencyLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPure
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSerene,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Meditative Rationale Quote
            Surface(
                color = CosmicVoid.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "“${recommendation.rationale}”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextCosmic,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏱ ${task.estimatedMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSerene
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "⚡ ${recommendation.energyHarmony}",
                        style = MaterialTheme.typography.labelSmall,
                        color = when (task.energyLevel) {
                            "HIGH" -> EnergyHigh
                            "LOW" -> EnergyLow
                            else -> EnergyMedium
                        }
                    )
                }

                Surface(
                    color = MangoWarm,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .testTag("start_what_to_do_button")
                        .clickable { onStartFocus(task) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = CosmicVoid,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (task.status == "PAUSED") "Resume" else "Start Focus",
                            style = MaterialTheme.typography.labelLarge,
                            color = CosmicVoid,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Resume Interrupted Task Banner
 * Displayed prominently when user returns and a task is in PAUSED state.
 */
@Composable
fun InterruptedTaskBanner(
    task: TaskEntity?,
    onResume: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (task == null || task.status != "PAUSED") return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("resume_interrupted_banner"),
        colors = CardDefaults.cardColors(containerColor = CosmicCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NebulaPink.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = NebulaPink.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Resume",
                        tint = NebulaPink,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "RESUME INTERRUPTED ACTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = NebulaPink,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPure,
                        maxLines = 1
                    )
                    Text(
                        text = "${task.actualMinutesSpent}m invested • Paused: ${task.interruptionReason ?: "External pause"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSerene,
                        maxLines = 1
                    )
                }
            }

            Surface(
                color = NebulaPink,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .testTag("resume_flow_button")
                    .clickable { onResume(task) }
            ) {
                Text(
                    text = "Resume",
                    style = MaterialTheme.typography.labelLarge,
                    color = CosmicVoid,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
