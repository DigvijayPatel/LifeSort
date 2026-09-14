package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyLevel
import com.example.data.remote.ExtractedTaskDto
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.EnergyHigh
import com.example.ui.theme.EnergyLow
import com.example.ui.theme.EnergyMedium
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.StarlightGold
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrainDumpScreen(
    inputText: String,
    onInputChanged: (String) -> Unit,
    isAnalyzing: Boolean,
    extractedTasks: List<ExtractedTaskDto>,
    onAnalyze: () -> Unit,
    onInstantAdd: () -> Unit,
    onSaveAll: () -> Unit,
    onClearExtracted: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPrompts = listOf(
        "Release mental clutter",
        "Key goals for today",
        "Deep work milestones",
        "Spiritual stillness & breath",
        "Health & body vitality"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("brain_dump_screen"),
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
                        text = "✦ NEW INTENTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = StarlightGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Add a new intention",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPure
                    )
                }
            }
        }

        // Explanation / Meditative Prompt
        item {
            Surface(
                color = CosmicSurfaceVariant,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🧘", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Empty your mind. The AI will structure your intentions automatically.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )
                }
            }
        }

        // Quick Inspiration Chips
        item {
            Column {
                Text(
                    text = "CONTEMPLATIVE THEMES",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCosmic,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickPrompts.forEach { prompt ->
                        Surface(
                            color = CosmicCard,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder),
                            modifier = Modifier.clickable {
                                val separator = if (inputText.isBlank()) "" else "\n"
                                onInputChanged("$inputText$separator$prompt: ")
                            }
                        ) {
                            Text(
                                text = "+ $prompt",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSerene,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Text Input Area
        item {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("brain_dump_text_field"),
                placeholder = {
                    Text(
                        text = "E.g. Need to build the cosmic timer canvas, prepare client presentation, meditate for 20 mins, call accountant, walk in the park...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CosmicSurface,
                    unfocusedContainerColor = CosmicSurface,
                    focusedBorderColor = StarlightGold,
                    unfocusedBorderColor = CosmicBorder,
                    focusedTextColor = TextPure,
                    unfocusedTextColor = TextPure
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Instant Save
                Surface(
                    color = if (inputText.isBlank()) CosmicSurfaceVariant else StarlightGold,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("instant_add_button")
                        .clickable(enabled = inputText.isNotBlank()) { onInstantAdd() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = if (inputText.isNotBlank()) CosmicVoid else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Intention",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (inputText.isNotBlank()) CosmicVoid else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Extract
                Surface(
                    color = if (isAnalyzing || inputText.isBlank()) CosmicSurfaceVariant else CosmicCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (inputText.isNotBlank()) StarlightGold else CosmicBorder),
                    modifier = Modifier
                        .testTag("transmute_button")
                        .clickable(enabled = !isAnalyzing && inputText.isNotBlank()) { onAnalyze() }
                ) {
                    Box(
                        modifier = Modifier.padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                color = StarlightGold,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Auto-Organize with AI",
                                tint = if (inputText.isNotBlank()) StarlightGold else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Extracted Tasks Preview Header
        if (extractedTasks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXTRACTED INTENTIONS (${extractedTasks.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onClearExtracted) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextMuted
                        )
                    }
                }
            }

            // Extracted items list
            items(extractedTasks) { item ->
                ExtractedTaskPreviewCard(item = item)
            }

            // Save All Button
            item {
                Surface(
                    color = SacredEmerald,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("commit_tasks_button")
                        .clickable { onSaveAll() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Commit",
                            tint = CosmicVoid,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save All Intentions",
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

@Composable
fun ExtractedTaskPreviewCard(
    item: ExtractedTaskDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CosmicCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.category.symbol} ${item.category.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCosmic
                )

                Surface(
                    color = when (item.energyLevel) {
                        EnergyLevel.HIGH -> EnergyHigh.copy(alpha = 0.2f)
                        EnergyLevel.LOW -> EnergyLow.copy(alpha = 0.2f)
                        else -> EnergyMedium.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.energyLevel.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (item.energyLevel) {
                            EnergyLevel.HIGH -> EnergyHigh
                            EnergyLevel.LOW -> EnergyLow
                            else -> EnergyMedium
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPure
            )

            if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSerene
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scores chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(color = CosmicSurfaceVariant, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "Impact: ${item.impactScore.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = StarlightGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(color = CosmicSurfaceVariant, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "Urgency: ${item.urgencyScore.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(color = CosmicSurfaceVariant, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "Est: ${item.estimatedMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSerene,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Phases Preview
            if (item.phases.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Planned Phases:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                item.phases.forEachIndexed { i, phase ->
                    Text(
                        text = "• Phase ${i + 1}: $phase",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}
