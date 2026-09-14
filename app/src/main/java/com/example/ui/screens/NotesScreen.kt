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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.MysticCyan
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
fun NotesScreen(
    notes: List<TaskNoteEntity>,
    tasks: List<TaskEntity>,
    onAddNote: (String?, String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Peaceful") }
    var selectedTaskId by remember { mutableStateOf<String?>(null) }

    val taskMap = tasks.associateBy { it.id }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("notes_screen"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            text = "INNER REFLECTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            color = StarlightGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Contemplation Sanctuary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPure
                        )
                    }
                }
            }

            if (notes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🕯️", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Contemplations Yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPure,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Capture your insights, realizations, or reflections from deep work sessions.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSerene
                            )
                        }
                    }
                }
            } else {
                items(notes, key = { it.id }) { note ->
                    val linkedTask = note.taskId?.let { taskMap[it] }
                    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Surface(
                                    color = when (note.reflectionMood) {
                                        "Ecstatic" -> StarlightGold.copy(alpha = 0.2f)
                                        "Focused" -> CelestialViolet.copy(alpha = 0.2f)
                                        "Turbulent" -> NebulaPink.copy(alpha = 0.2f)
                                        else -> SacredEmerald.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = note.reflectionMood,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (note.reflectionMood) {
                                            "Ecstatic" -> StarlightGold
                                            "Focused" -> CelestialViolet
                                            "Turbulent" -> NebulaPink
                                            else -> SacredEmerald
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Text(
                                    text = dateFormat.format(Date(note.createdAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPure
                            )

                            if (linkedTask != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = CosmicSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "⚡ Linked: ${linkedTask.title}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextCosmic,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Note FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = StarlightGold,
            contentColor = CosmicVoid,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 28.dp)
                .size(56.dp)
                .testTag("fab_add_note")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Reflection",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showAddDialog) {
        val moods = listOf("Peaceful", "Ecstatic", "Focused", "Transmuted", "Turbulent")
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CosmicCard,
            title = { Text("Consecrate an Insight", color = TextPure) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Your reflection...", color = TextSerene) },
                        modifier = Modifier.height(130.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = StarlightGold,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )

                    Text("Emotional / Vital State:", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        moods.forEach { mood ->
                            Surface(
                                color = if (selectedMood == mood) StarlightGold else CosmicSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { selectedMood = mood }
                            ) {
                                Text(
                                    text = mood,
                                    color = if (selectedMood == mood) CosmicVoid else TextSerene,
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
                        onAddNote(selectedTaskId, noteText, selectedMood)
                        noteText = ""
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StarlightGold)
                ) {
                    Text("Save to Sanctuary", color = CosmicVoid, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSerene)
                }
            }
        )
    }
}
