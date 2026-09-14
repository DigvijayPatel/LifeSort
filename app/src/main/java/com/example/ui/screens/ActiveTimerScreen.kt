package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPhaseEntity
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.EtherealPurple
import com.example.ui.theme.MysticCyan
import com.example.ui.theme.MangoDeep
import com.example.ui.theme.MangoSoft
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.StarlightGold
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene
import java.util.Locale

@Composable
fun ActiveTimerScreen(
    task: TaskEntity?,
    phases: List<TaskPhaseEntity>,
    remainingSeconds: Int,
    elapsedSeconds: Int,
    isRunning: Boolean,
    companionMessage: String?,
    onPause: (String) -> Unit,
    onResume: () -> Unit,
    onComplete: (String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (task == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Focus session not active.", color = TextSerene)
        }
        return
    }

    var showInterruptionDialog by remember { mutableStateOf(false) }
    var selectedInterruptionReason by remember { mutableStateOf("Spontaneous Call") }
    var customInterruptionReason by remember { mutableStateOf("") }

    var showCompleteDialog by remember { mutableStateOf(false) }
    var completionReflection by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "TimerBreath")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val currentPhase = phases.firstOrNull { !it.isCompleted }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    val elapsedMinutes = elapsedSeconds / 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .testTag("active_timer_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPure
                )
            }

            Surface(
                color = CosmicCard,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Pranic Flow",
                        tint = MangoWarm,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRunning) "Deep Immersion" else "Paused in Stillness",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isRunning) SacredEmerald else NebulaPink,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Active Mission & Current Phase
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPure,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            if (currentPhase != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = CosmicSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                ) {
                    Text(
                        text = "Current Phase: ${currentPhase.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MangoWarm,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$elapsedMinutes min invested in focused presence",
                style = MaterialTheme.typography.labelSmall,
                color = TextSerene
            )
        }

        // Center Breathing Timer Canvas
        Box(
            modifier = Modifier
                .size(280.dp)
                .testTag("timer_canvas_box"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2f) * 0.78f * pulseScale

                // Outer aura glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CelestialViolet.copy(alpha = 0.35f),
                            EtherealPurple.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.4f
                    ),
                    radius = radius * 1.4f,
                    center = center
                )

                // Track ring
                drawCircle(
                    color = CosmicSurfaceVariant,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )

                // Active progress arc
                val totalTarget = (task.estimatedMinutes * 60).coerceAtLeast(1)
                val progressFraction = (elapsedSeconds.toFloat() / totalTarget).coerceIn(0f, 1f)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            MangoWarm,
                            MangoDeep,
                            MysticCyan,
                            MangoWarm
                        ),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * (1f - (remainingSeconds.toFloat() / totalTarget).coerceIn(0f, 1f)),
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRunning) "Breathe Deeply" else "Flow Paused",
                    style = MaterialTheme.typography.labelSmall,
                    color = MangoWarm,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // Companion Guidance
        if (companionMessage != null) {
            Surface(
                color = CosmicCard.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StarlightGold.copy(alpha = 0.3f)),
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Companion",
                        tint = StarlightGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = companionMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Text(
                text = "“Whatever you are doing, just be involved with all your heart. Life responds to intensity, not calculation.”",
                style = MaterialTheme.typography.bodyMedium,
                color = TextCosmic.copy(alpha = 0.8f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause / Resume Button
            if (isRunning) {
                Surface(
                    color = CosmicCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NebulaPink.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("pause_timer_button")
                        .clickable { showInterruptionDialog = true }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = NebulaPink
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pause Flow",
                            color = NebulaPink,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Surface(
                    color = MangoWarm,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("resume_timer_button")
                        .clickable { onResume() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume",
                            tint = CosmicVoid
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resume Flow",
                            color = CosmicVoid,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Complete Task Button
            Surface(
                color = SacredEmerald,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("complete_task_timer_button")
                    .clickable { showCompleteDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = CosmicVoid
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complete Action",
                        color = CosmicVoid,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Interruption Reason Dialog (Pause Flow)
    if (showInterruptionDialog) {
        val reasons = listOf("Spontaneous Call", "Physical Rest Needed", "Urgent Distraction", "Mental Wander", "Other")
        AlertDialog(
            onDismissRequest = { showInterruptionDialog = false },
            containerColor = CosmicCard,
            title = {
                Text(
                    text = "Pause Conscious Flow",
                    color = TextPure,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Acknowledge the reason for pausing so your momentum can be restored gracefully when you return:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )

                    reasons.forEach { reason ->
                        Surface(
                            color = if (selectedInterruptionReason == reason) NebulaPink.copy(alpha = 0.2f) else CosmicSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = if (selectedInterruptionReason == reason) androidx.compose.foundation.BorderStroke(1.dp, NebulaPink) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedInterruptionReason = reason }
                        ) {
                            Text(
                                text = reason,
                                color = if (selectedInterruptionReason == reason) NebulaPink else TextPure,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    if (selectedInterruptionReason == "Other") {
                        OutlinedTextField(
                            value = customInterruptionReason,
                            onValueChange = { customInterruptionReason = it },
                            label = { Text("Specify Reason", color = TextSerene) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPure,
                                unfocusedTextColor = TextPure,
                                focusedBorderColor = NebulaPink,
                                unfocusedBorderColor = CosmicBorder
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reason = if (selectedInterruptionReason == "Other" && customInterruptionReason.isNotBlank()) {
                            customInterruptionReason
                        } else {
                            selectedInterruptionReason
                        }
                        onPause(reason)
                        showInterruptionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NebulaPink)
                ) {
                    Text("Record & Pause", color = CosmicVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInterruptionDialog = false }) {
                    Text("Cancel", color = TextSerene)
                }
            }
        )
    }

    // Completion Reflection Dialog
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            containerColor = CosmicCard,
            title = {
                Text(
                    text = "Action Fulfilled! 🎉",
                    color = SacredEmerald,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Take a moment to consecrate this completion. What insight or clarity arose during this focus session?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )
                    OutlinedTextField(
                        value = completionReflection,
                        onValueChange = { completionReflection = it },
                        placeholder = { Text("Optional reflection...", color = TextMuted) },
                        modifier = Modifier.height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = SacredEmerald,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onComplete(completionReflection.takeIf { it.isNotBlank() })
                        showCompleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SacredEmerald)
                ) {
                    Text("Celebrate Completion", color = CosmicVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Skip", color = TextSerene)
                }
            }
        )
    }
}
