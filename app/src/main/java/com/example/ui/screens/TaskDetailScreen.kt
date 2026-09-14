package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.EnergyHigh
import com.example.ui.theme.EnergyLow
import com.example.ui.theme.EnergyMedium
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.StarlightGold
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskDetailScreen(
    task: TaskEntity?,
    phases: List<TaskPhaseEntity>,
    notes: List<TaskNoteEntity>,
    onTogglePhase: (String, Boolean) -> Unit,
    onAddPhase: (String, String, Int) -> Unit,
    onAddNote: (String, String, String) -> Unit,
    onStartFocus: (TaskEntity) -> Unit,
    onResumeFocus: (TaskEntity) -> Unit,
    onCompleteTask: (String) -> Unit,
    onScheduleTask: (String, Long) -> Unit,
    onDeleteTask: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (task == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Action not found.", color = TextSerene)
        }
        return
    }

    var showAddPhaseDialog by remember { mutableStateOf(false) }
    var newPhaseTitle by remember { mutableStateOf("") }
    var newPhaseMinutes by remember { mutableStateOf("15") }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteContent by remember { mutableStateOf("") }
    var noteMood by remember { mutableStateOf("Peaceful") }

    val completedPhases = phases.count { it.isCompleted }
    val progress = if (phases.isNotEmpty()) completedPhases.toFloat() / phases.size else 0f
    val category = TaskCategory.fromString(task.category)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("task_detail_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Navigation Bar
        item {
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

                Row {
                    IconButton(onClick = { onDeleteTask(task.id); onBack() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted
                        )
                    }
                }
            }
        }

        // Title and Category Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${category.symbol} ${category.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = when (task.energyLevel) {
                            "HIGH" -> EnergyHigh.copy(alpha = 0.2f)
                            "LOW" -> EnergyLow.copy(alpha = 0.2f)
                            else -> EnergyMedium.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = task.energyLevel,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (task.energyLevel) {
                                "HIGH" -> EnergyHigh
                                "LOW" -> EnergyLow
                                else -> EnergyMedium
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPure
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSerene
                    )
                }
            }
        }

        // Paused Interruption Alert (if paused)
        if (task.status == "PAUSED") {
            item {
                Surface(
                    color = NebulaPink.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NebulaPink.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = "Paused",
                            tint = NebulaPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Action Currently Paused",
                                style = MaterialTheme.typography.labelMedium,
                                color = NebulaPink,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${task.actualMinutesSpent}m elapsed • Reason: ${task.interruptionReason ?: "External pause"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSerene
                            )
                        }
                    }
                }
            }
        }

        // AI Cosmic Rationale
        if (task.aiRationale.isNotBlank()) {
            item {
                Surface(
                    color = CosmicCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COSMIC RATIONALE",
                            style = MaterialTheme.typography.labelSmall,
                            color = StarlightGold,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "“${task.aiRationale}”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextCosmic,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        // Scores Breakdown Card
        item {
            Card(
                modifier = Modifier.animateContentSize().fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ScoreColumn("Impact", "${task.impactScore.toInt()}", StarlightGold)
                    ScoreColumn("Urgency", "${task.urgencyScore.toInt()}", NebulaPink)
                    ScoreColumn("Clarity", "${task.clarityScore.toInt()}", SacredEmerald)
                    ScoreColumn("Rank", String.format(Locale.getDefault(), "%.1f", task.computedRank), CelestialViolet)
                }
            }
        }

        // Stepping-Stone Phases Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PHASE PLANNER",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$completedPhases of ${phases.size} phases complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                TextButton(onClick = { showAddPhaseDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Phase",
                        tint = StarlightGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Phase",
                        color = StarlightGold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        if (phases.isNotEmpty()) {
            item {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = StarlightGold,
                    trackColor = CosmicSurfaceVariant
                )
            }
        }

        items(phases, key = { it.id }) { phase ->
            Card(
                modifier = Modifier.animateContentSize().fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (phase.isCompleted) CosmicSurfaceVariant.copy(alpha = 0.5f) else CosmicCard
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = phase.isCompleted,
                        onCheckedChange = { onTogglePhase(phase.id, it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SacredEmerald,
                            checkmarkColor = CosmicVoid,
                            uncheckedColor = TextMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = phase.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (phase.isCompleted) TextMuted else TextPure
                        )
                        Text(
                            text = "⏱ ${phase.targetMinutes}m target",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSerene
                        )
                    }
                }
            }
        }

        // Context & Contemplation Notes Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONTEMPLATION NOTES (${notes.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCosmic,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { showAddNoteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note",
                        tint = StarlightGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Note",
                        color = StarlightGold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        items(notes, key = { it.id }) { note ->
            val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            Card(
                modifier = Modifier.animateContentSize().fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = CosmicSurfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = note.reflectionMood,
                                style = MaterialTheme.typography.labelSmall,
                                color = StarlightGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = dateFormat.format(Date(note.createdAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPure
                    )
                }
            }
        }

        // Bottom Action Controls
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (task.status != "COMPLETED") {
                    Button(
                        onClick = {
                            if (task.status == "PAUSED") onResumeFocus(task) else onStartFocus(task)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StarlightGold),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = CosmicVoid
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (task.status == "PAUSED") "Resume Focus Session" else "Start Focus Session",
                            color = CosmicVoid,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                // Simple default logic for scheduling 1 hour from now for prototype
                                val scheduledTime = System.currentTimeMillis() + (60 * 60 * 1000L)
                                onScheduleTask(task.id, scheduledTime)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Schedule",
                                tint = StarlightGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remind in 1h", color = StarlightGold, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { onCompleteTask(task.id); onBack() },
                            colors = ButtonDefaults.buttonColors(containerColor = SacredEmerald),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Complete",
                                tint = CosmicVoid
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Complete",
                                color = CosmicVoid,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Phase Dialog
    if (showAddPhaseDialog) {
        AlertDialog(
            onDismissRequest = { showAddPhaseDialog = false },
            containerColor = CosmicCard,
            title = { Text("Add Sub-Phase", color = TextPure) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newPhaseTitle,
                        onValueChange = { newPhaseTitle = it },
                        label = { Text("Phase Name", color = TextSerene) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = StarlightGold,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )
                    OutlinedTextField(
                        value = newPhaseMinutes,
                        onValueChange = { newPhaseMinutes = it },
                        label = { Text("Target Minutes", color = TextSerene) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = StarlightGold,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = newPhaseMinutes.toIntOrNull() ?: 15
                        onAddPhase(task.id, newPhaseTitle, mins)
                        newPhaseTitle = ""
                        showAddPhaseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StarlightGold)
                ) {
                    Text("Add", color = CosmicVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPhaseDialog = false }) {
                    Text("Cancel", color = TextSerene)
                }
            }
        )
    }

    // Add Contemplation Note Dialog
    if (showAddNoteDialog) {
        val moods = listOf("Peaceful", "Ecstatic", "Focused", "Turbulent", "Transmuted")
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            containerColor = CosmicCard,
            title = { Text("Add Contemplation Note", color = TextPure) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Insight or reflection...", color = TextSerene) },
                        modifier = Modifier.height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = StarlightGold,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        moods.take(3).forEach { mood ->
                            Surface(
                                color = if (noteMood == mood) StarlightGold else CosmicSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { noteMood = mood }
                            ) {
                                Text(
                                    text = mood,
                                    color = if (noteMood == mood) CosmicVoid else TextSerene,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddNote(task.id, noteContent, noteMood)
                        noteContent = ""
                        showAddNoteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StarlightGold)
                ) {
                    Text("Save Note", color = CosmicVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("Cancel", color = TextSerene)
                }
            }
        )
    }
}

@Composable
fun ScoreColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
