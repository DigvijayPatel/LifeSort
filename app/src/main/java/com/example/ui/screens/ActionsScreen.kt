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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.data.model.EnergyLevel
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPhaseEntity
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.EnergyHigh
import com.example.ui.theme.EnergyLow
import com.example.ui.theme.EnergyMedium
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene

import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Delete

@Composable
fun ActionsScreen(
    activeTasks: List<TaskEntity>,
    completedTasks: List<TaskEntity>,
    deletedTasks: List<TaskEntity>,
    onRestoreTask: (String) -> Unit,
    phases: List<TaskPhaseEntity>,
    onTaskClick: (String) -> Unit,
    onStartFocus: (TaskEntity) -> Unit,
    onQuickCompleteTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onTogglePhase: (String, Boolean) -> Unit,
    onOpenBrainDump: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>("ALL") }
    val categories = listOf("ALL", "WORK", "SPIRITUAL", "HEALTH", "PERSONAL")

    val filteredTasks = if (selectedCategory == "ALL" || selectedCategory == null) {
        activeTasks
    } else {
        activeTasks.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    var selectedWindow by remember { mutableStateOf("Intentions") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CosmicCard,
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                NavigationDrawerItem(
                    label = { Text("Intentions") },
                    selected = selectedWindow == "Intentions",
                    onClick = {
                        selectedWindow = "Intentions"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MangoWarm.copy(alpha = 0.2f),
                        selectedTextColor = MangoWarm,
                        unselectedTextColor = TextPure
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text("Actions") },
                    selected = selectedWindow == "Actions",
                    onClick = {
                        selectedWindow = "Actions"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MangoWarm.copy(alpha = 0.2f),
                        selectedTextColor = MangoWarm,
                        unselectedTextColor = TextPure
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text("Recycle Bin") },
                    selected = selectedWindow == "Recycle Bin",
                    onClick = {
                        selectedWindow = "Recycle Bin"
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MangoWarm.copy(alpha = 0.2f),
                        selectedTextColor = MangoWarm,
                        unselectedTextColor = TextPure
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        if (selectedWindow == "Actions") {
            TaskHistoryScreen(
                completedTasks = completedTasks,
                onBack = { scope.launch { drawerState.open() } },
                isMenu = true
            )
        } else if (selectedWindow == "Recycle Bin") {
            RecycleBinScreen(
                deletedTasks = deletedTasks,
                onRestoreTask = onRestoreTask,
                onBack = { scope.launch { drawerState.open() } }
            )
        } else {
            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("actions_screen_content"),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("menu_actions")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = TextPure
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✦ Intentions",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = TextPure
                            )
                        }
                    }

                    // Category Filter Pills
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { cat ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    color = if (isSelected) MangoWarm else CosmicCard,
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MangoWarm else CosmicBorder
                                    ),
                                    modifier = Modifier.clickable { selectedCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) CosmicVoid else TextSerene,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        letterSpacing = 0.6.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Task List
                    if (filteredTasks.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "✨", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No tasks in this category",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPure
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "All tasks in this category are completed.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSerene
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredTasks, key = { it.id }) { task ->
                            val taskPhases = phases.filter { it.taskId == task.id }
                            ActionBacklogCard(
                                modifier = Modifier.animateItem(),
                                task = task,
                                phases = taskPhases,
                                onClick = { onTaskClick(task.id) },
                                onStartFocus = { onStartFocus(task) },
                                onQuickComplete = { onQuickCompleteTask(task.id) },
                                onDeleteTask = { onDeleteTask(task.id) },
                                onTogglePhase = onTogglePhase
                            )
                        }
                    }
                }

                // Floating Action Button
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 24.dp)
                        .size(56.dp)
                        .clickable { onOpenBrainDump() }
                        .testTag("fab_actions_add")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(CelestialViolet, com.example.ui.theme.MangoDeep, com.example.ui.theme.MysticCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Add Task",
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionBacklogCard(
    task: TaskEntity,
    phases: List<TaskPhaseEntity>,
    onClick: () -> Unit,
    onStartFocus: () -> Unit,
    onQuickComplete: () -> Unit,
    onDeleteTask: () -> Unit,
    onTogglePhase: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val category = TaskCategory.fromString(task.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick)
            .testTag("action_item_${task.id}"),
        colors = CardDefaults.cardColors(containerColor = CosmicCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                IconButton(
                    onClick = onQuickComplete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Mark Complete",
                        tint = MangoWarm,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPure,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MangoWarm,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = " • ",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${task.estimatedMinutes}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSerene
                        )
                        Text(
                            text = " • ",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = EnergyLevel.fromString(task.energyLevel).label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteTask,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Focus Action Button
                Surface(
                    color = MangoWarm.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.clickable(onClick = onStartFocus)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = MangoWarm,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.labelSmall,
                            color = MangoWarm,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Subphases snippet if any
            if (phases.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 42.dp)
                ) {
                    phases.take(2).forEach { phase ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTogglePhase(phase.id, !phase.isCompleted) }
                        ) {
                            Icon(
                                imageVector = if (phase.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (phase.isCompleted) SacredEmerald else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = phase.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (phase.isCompleted) TextMuted else TextSerene,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (phases.size > 2) {
                        Text(
                            text = "+${phases.size - 2} more phases",
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
