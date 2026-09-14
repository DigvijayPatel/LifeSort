package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.domain.gamification.LifeLevel
import com.example.domain.gamification.LifeMilestone
import com.example.domain.gamification.LifeRpgEngine
import com.example.ui.components.BreathingLuminousOrb
import com.example.ui.components.HistoricalLifeGrowthGraph
import com.example.ui.components.VitalityMetricsRow
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.MysticCyan
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene

@Composable
fun LifeGrowthScreen(
    latestSnapshot: LifeGrowthSnapshotEntity?,
    history: List<LifeGrowthSnapshotEntity>,
    currentLevel: LifeLevel = LifeRpgEngine.getLevelForScore(latestSnapshot?.growthScore ?: 780),
    unlockedMilestones: List<LifeMilestone> = emptyList(),
    activeStreakDays: Int = 5,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScore = latestSnapshot?.growthScore ?: 780
    val focusPts = latestSnapshot?.mindfulFocusScore ?: 270
    val harmonyPts = latestSnapshot?.executionHarmonyScore ?: 255
    val clarityPts = latestSnapshot?.mentalClarityScore ?: 165
    val momentumPts = latestSnapshot?.cosmicMomentumScore ?: 145

    val allMilestones = LifeRpgEngine.ALL_MILESTONES
    val unlockedIds = unlockedMilestones.map { it.id }.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("life_growth_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPure
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "✦ LIFE GROWTH JOURNEY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MangoWarm,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Level ${currentLevel.level} • ${currentLevel.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPure
                    )
                }
            }
        }

        // Hero Orb with clear score & level
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BreathingLuminousOrb(
                    growthScore = currentScore,
                    growthLevel = currentLevel.title,
                    levelNumber = currentLevel.level
                )
            }
        }

        // Level Evolution & Archetype Description
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ASTRAL CONSCIOUSNESS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextCosmic,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = MangoWarm.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${currentLevel.ringsCount} Sacred Mandala Rings",
                                style = MaterialTheme.typography.labelSmall,
                                color = MangoWarm,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentLevel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPure.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress to next tier
                    val progressToNext = LifeRpgEngine.getProgressToNextLevel(currentScore)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tier Mastery",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSerene
                        )
                        Text(
                            text = "${(progressToNext * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MangoWarm,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progressToNext },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MangoWarm,
                        trackColor = CosmicSurfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // Sacred Orb Evolution Tiers Gallery
        item {
            Column {
                Text(
                    text = "MANDALA EVOLUTION TIERS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCosmic,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LifeRpgEngine.LEVELS) { tier ->
                        val isUnlocked = currentScore >= tier.minScore
                        val isCurrent = tier.level == currentLevel.level

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) CosmicSurfaceVariant else CosmicCard
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCurrent) MangoWarm else CosmicBorder
                            ),
                            modifier = Modifier.width(140.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isUnlocked) MangoWarm.copy(alpha = 0.2f) else CosmicSurfaceVariant,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUnlocked) {
                                        Text(
                                            text = "${tier.level}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MangoWarm,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = tier.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isUnlocked) TextPure else TextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )

                                Text(
                                    text = "${tier.minScore} pts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Milestones & Conscious Achievements
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONSCIOUS MILESTONES",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${unlockedIds.size} / ${allMilestones.size} Unlocked",
                        style = MaterialTheme.typography.labelSmall,
                        color = MangoWarm
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                for (milestone in allMilestones) {
                    val isUnlocked = unlockedIds.contains(milestone.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) CosmicCard else CosmicCard.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isUnlocked) SacredEmerald.copy(alpha = 0.5f) else CosmicBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (isUnlocked) SacredEmerald.copy(alpha = 0.2f) else CosmicSurfaceVariant,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = milestone.icon,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = milestone.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = if (isUnlocked) TextPure else TextMuted
                                )
                                Text(
                                    text = milestone.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isUnlocked) TextSerene else TextMuted.copy(alpha = 0.6f)
                                )
                            }

                            Surface(
                                color = if (isUnlocked) SacredEmerald.copy(alpha = 0.15f) else CosmicSurfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isUnlocked) SacredEmerald else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Vitality Summary Pills
        item {
            VitalityMetricsRow(
                clarityScore = "+$clarityPts",
                momentumScore = "+$momentumPts",
                lifeValueScore = "+${(focusPts + harmonyPts) / 10}"
            )
        }

        // 7-Day Growth Trajectory Graph
        item {
            Column {
                Text(
                    text = "GROWTH OVER TIME",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCosmic,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                HistoricalLifeGrowthGraph(history = history)
            }
        }

        // 4 Pillars of Daily Progress Breakdown
        item {
            Text(
                text = "4 PILLARS OF PROGRESS",
                style = MaterialTheme.typography.labelSmall,
                color = TextCosmic,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            PillarMetricCard(
                title = "Mindful Focus",
                points = focusPts,
                maxPoints = 300,
                icon = Icons.Default.Spa,
                accentColor = SacredEmerald,
                description = "Presence during single-task flow sessions without task-switching."
            )
        }

        item {
            PillarMetricCard(
                title = "Execution Harmony",
                points = harmonyPts,
                maxPoints = 300,
                icon = Icons.Default.Bolt,
                accentColor = MangoWarm,
                description = "Honoring natural energy cycles and finishing started commitments."
            )
        }

        item {
            PillarMetricCard(
                title = "Mental Clarity",
                points = clarityPts,
                maxPoints = 200,
                icon = Icons.Default.FilterDrama,
                accentColor = MysticCyan,
                description = "Transforming unorganized mind thoughts into calm, structured actions."
            )
        }

        item {
            PillarMetricCard(
                title = "Cosmic Momentum",
                points = momentumPts,
                maxPoints = 200,
                icon = Icons.Default.AutoAwesome,
                accentColor = CelestialViolet,
                description = "Maintaining continuous mindful momentum across days."
            )
        }
    }
}

@Composable
fun PillarMetricCard(
    title: String,
    points: Int,
    maxPoints: Int,
    icon: ImageVector,
    accentColor: Color,
    description: String,
    modifier: Modifier = Modifier
) {
    val progress = (points.toFloat() / maxPoints).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CosmicCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPure
                    )
                }

                Text(
                    text = "$points / $maxPoints pts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSerene,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = accentColor,
                trackColor = CosmicSurfaceVariant
            )
        }
    }
}
