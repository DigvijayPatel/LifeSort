package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyLevel
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.domain.engine.Recommendation
import com.example.domain.gamification.CompletionReward
import com.example.domain.gamification.LifeLevel
import com.example.domain.gamification.LifeRpgEngine
import com.example.ui.components.BreathingLuminousOrb
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.MangoDeep
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.MysticCyan
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene

/**
 * Radically Simplified Miracle-of-Mind Inspired Home Screen
 * Answers ONLY one question: "What should I do now?"
 * Features:
 * - One breathing Life Growth orb with centered score
 * - One recommended task based on intelligent decision engine
 * - ONE primary action button
 * - Subtle gamified progression & celebration burst feedback
 */
@Composable
fun HomeScreen(
    latestSnapshot: LifeGrowthSnapshotEntity?,
    growthHistory: List<LifeGrowthSnapshotEntity>,
    recommendation: Recommendation?,
    pausedTask: TaskEntity?,
    activeTasks: List<TaskEntity>,
    currentLevel: LifeLevel,
    levelProgress: Float,
    activeStreakDays: Int,
    lastReward: CompletionReward?,
    onTaskClick: (String) -> Unit,
    onStartFocus: (TaskEntity) -> Unit,
    onResumePausedTask: (TaskEntity) -> Unit,
    onQuickCompleteTask: (String) -> Unit,
    onOpenActions: () -> Unit,
    onOpenJourney: () -> Unit,
    onOpenBrainDump: () -> Unit,
    onDismissCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val growthScore = lastReward?.newScore ?: (latestSnapshot?.growthScore ?: 780)
    val isCelebration = lastReward != null
    val celebrationXp = lastReward?.xpGained

    // Choose the single recommended action to answer "What should I do now?"
    val primaryTask = pausedTask ?: recommendation?.task ?: activeTasks.firstOrNull()
    val isPaused = pausedTask != null

    val remainingCount = activeTasks.filter { it.id != primaryTask?.id }.size

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .animateContentSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .testTag("home_screen_content"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Centerpiece: The Living Breathing Luminous Orb
            BreathingLuminousOrb(
                growthScore = growthScore,
                growthLevel = currentLevel.title,
                levelNumber = currentLevel.level,
                isCelebration = isCelebration,
                celebrationXp = celebrationXp,
                onClick = onOpenJourney
            )

            Spacer(modifier = Modifier.height(32.dp))

            // THE QUESTION: "WHAT SHOULD I DO NOW?"
            Text(
                text = "Next up",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.0.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = TextCosmic
            )

            Spacer(modifier = Modifier.height(16.dp))

            // THE ONE RECOMMENDED TASK CARD
            if (primaryTask != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = primaryTask.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 32.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = TextPure,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${primaryTask.estimatedMinutes} min • ${if (isPaused) "Paused" else "Recommended"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ONE PRIMARY ACTION BUTTON
                Surface(
                    color = MangoWarm,
                    shape = RoundedCornerShape(26.dp),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable {
                            if (pausedTask != null) {
                                onResumePausedTask(pausedTask)
                            } else {
                                onStartFocus(primaryTask)
                            }
                        }
                        .testTag("hero_primary_action")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Primary Action",
                            tint = CosmicVoid,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPaused) {
                                "Resume Task • ${primaryTask.estimatedMinutes}m"
                            } else {
                                "Start Task • ${primaryTask.estimatedMinutes}m"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            ),
                            color = CosmicVoid
                        )
                    }
                }
            } else {
                // All clear contemplative state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✦",
                        fontSize = 32.sp,
                        color = MangoWarm
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You're all caught up!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPure,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    color = MangoWarm,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { onOpenBrainDump() }
                        .testTag("hero_primary_action")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add Task",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = CosmicVoid
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECONDARY NAVIGATION AFFORDANCE
            Text(
                text = if (remainingCount > 0) "View $remainingCount other tasks →" else "View all tasks →",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSerene,
                modifier = Modifier
                    .clickable { onOpenActions() }
                    .padding(8.dp)
                    .testTag("secondary_actions_link")
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Floating Action Button for adding tasks
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .size(56.dp)
                .clickable { onOpenBrainDump() }
                .testTag("fab_home_add")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(CelestialViolet, MangoDeep, MysticCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Add Task",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // 8. Rewarding Celebration Toast / Level-up Notification
        AnimatedVisibility(
            visible = isCelebration,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MangoWarm),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDismissCelebration() }
                    .testTag("celebration_toast")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lastReward?.leveledUp == true) {
                                "LEVELED UP! Level ${currentLevel.level} • ${lastReward.newLevelTitle}"
                            } else {
                                "Flow Complete: +${lastReward?.xpGained} Life Growth"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = MangoWarm,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (lastReward?.unlockedMilestone != null) {
                                "Milestone Unlocked: ${lastReward.unlockedMilestone.title}!"
                            } else {
                                "Your Life Growth orb expands with renewed vitality."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPure
                        )
                    }
                }
            }
        }
    }
}
