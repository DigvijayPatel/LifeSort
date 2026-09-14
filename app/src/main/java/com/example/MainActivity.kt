package com.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CosmicBackground
import com.example.ui.screens.ActionsScreen
import com.example.ui.screens.ActiveTimerScreen
import com.example.ui.screens.BrainDumpScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LifeGrowthScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.screens.TaskHistoryScreen
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.LifeSortTheme
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.StarlightGold
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.viewmodel.LifeSortViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            LifeSortTheme {
                LifeSortApp()
            }
        }
    }
}

@Composable
fun LifeSortApp(
    viewModel: LifeSortViewModel = viewModel()
) {

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val deletedTasks by viewModel.deletedTasks.collectAsState()
    val allPhases by viewModel.allPhases.collectAsState()
    val allNotes by viewModel.allNotes.collectAsState()
    val latestSnapshot by viewModel.latestSnapshot.collectAsState()
    val growthHistory by viewModel.growthHistory.collectAsState()
    val pausedTask by viewModel.latestPausedTask.collectAsState()
    val recommendation by viewModel.recommendation.collectAsState()

    // Gamification / RPG states
    val currentRpgLevel by viewModel.currentRpgLevel.collectAsState()
    val levelProgress by viewModel.progressToNextLevel.collectAsState()
    val activeStreakDays by viewModel.activeStreakDays.collectAsState()
    val lastReward by viewModel.lastCompletionReward.collectAsState()
    val unlockedMilestones by viewModel.unlockedMilestones.collectAsState()

    // Brain dump states
    val brainDumpInput by viewModel.brainDumpInput.collectAsState()
    val isAnalyzingBrainDump by viewModel.isAnalyzingBrainDump.collectAsState()
    val extractedTasks by viewModel.extractedTasks.collectAsState()

    // Timer states
    val timerRemaining by viewModel.timerRemainingSeconds.collectAsState()
    val timerElapsed by viewModel.timerElapsedSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val companionMessage by viewModel.companionMessage.collectAsState()

    // Settings / Test states
    val supabaseStatus by viewModel.supabaseStatus.collectAsState()
    val testResults by viewModel.testResults.collectAsState()
    val isRunningTests by viewModel.isRunningTests.collectAsState()

    val showBottomBar = currentScreen is Screen.Home ||
            currentScreen is Screen.Actions ||
            currentScreen is Screen.LifeGrowth ||
            currentScreen is Screen.Notes ||
            currentScreen is Screen.History ||
            currentScreen is Screen.Settings

    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateTo(Screen.Home)
    }

    CosmicBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showBottomBar,
                    enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) + androidx.compose.animation.fadeOut()
                ) {
                    CosmicBottomNavigationBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .animateContentSize()
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                    },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                    is Screen.Home -> {
                        HomeScreen(
                            latestSnapshot = latestSnapshot,
                            growthHistory = growthHistory,
                            recommendation = recommendation,
                            pausedTask = pausedTask,
                            activeTasks = activeTasks,
                            currentLevel = currentRpgLevel,
                            levelProgress = levelProgress,
                            activeStreakDays = activeStreakDays,
                            lastReward = lastReward,
                            onTaskClick = { taskId -> viewModel.navigateTo(Screen.TaskDetail(taskId)) },
                            onStartFocus = { task -> viewModel.startTimer(task.id, task.estimatedMinutes) },
                            onResumePausedTask = { task -> viewModel.resumeTimer(task.id) },
                            onQuickCompleteTask = { taskId -> viewModel.completeTaskDirectly(taskId) },
                            onOpenActions = { viewModel.navigateTo(Screen.Actions) },
                            onOpenJourney = { viewModel.navigateTo(Screen.LifeGrowth) },
                            onOpenBrainDump = { viewModel.navigateTo(Screen.BrainDump) },
                            onDismissCelebration = { viewModel.dismissCelebration() }
                        )
                    }

                    is Screen.Actions -> {
                        ActionsScreen(
                            activeTasks = activeTasks,
                            completedTasks = completedTasks,
                            deletedTasks = deletedTasks,
                            onRestoreTask = { taskId -> viewModel.restoreTask(taskId) },
                            phases = allPhases,
                            onTaskClick = { taskId -> viewModel.navigateTo(Screen.TaskDetail(taskId)) },
                            onStartFocus = { task -> viewModel.startTimer(task.id, task.estimatedMinutes) },
                            onQuickCompleteTask = { taskId -> viewModel.completeTaskDirectly(taskId) },
                            onDeleteTask = { taskId -> viewModel.deleteTask(taskId) },
                            onTogglePhase = { phaseId, done -> viewModel.togglePhase(phaseId, done) },
                            onOpenBrainDump = { viewModel.navigateTo(Screen.BrainDump) },
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.BrainDump -> {
                        BrainDumpScreen(
                            inputText = brainDumpInput,
                            onInputChanged = { viewModel.updateBrainDumpInput(it) },
                            isAnalyzing = isAnalyzingBrainDump,
                            extractedTasks = extractedTasks,
                            onAnalyze = { viewModel.analyzeBrainDump() },
                            onInstantAdd = { viewModel.instantAddIntention() },
                            onSaveAll = { viewModel.saveExtractedTasksToSanctuary() },
                            onClearExtracted = { viewModel.clearExtractedTasks() },
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.TaskDetail -> {
                        val task = activeTasks.firstOrNull { it.id == screen.taskId }
                            ?: completedTasks.firstOrNull { it.id == screen.taskId }
                        val phases = allPhases.filter { it.taskId == screen.taskId }
                        val notes = allNotes.filter { it.taskId == screen.taskId }

                        TaskDetailScreen(
                            task = task,
                            phases = phases,
                            notes = notes,
                            onTogglePhase = { phaseId, done -> viewModel.togglePhase(phaseId, done) },
                            onAddPhase = { taskId, title, mins -> viewModel.addPhase(taskId, title, mins) },
                            onAddNote = { taskId, content, mood -> viewModel.addNote(taskId, content, mood) },
                            onStartFocus = { t -> viewModel.startTimer(t.id, t.estimatedMinutes) },
                            onResumeFocus = { t -> viewModel.resumeTimer(t.id) },
                            onCompleteTask = { taskId -> viewModel.completeTaskDirectly(taskId) },
                            onScheduleTask = { taskId, time -> viewModel.scheduleTask(taskId, time) },
                            onDeleteTask = { taskId -> viewModel.deleteTask(taskId) },
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.ActiveTimer -> {
                        val task = activeTasks.firstOrNull { it.id == screen.taskId }
                        val phases = allPhases.filter { it.taskId == screen.taskId }

                        ActiveTimerScreen(
                            task = task,
                            phases = phases,
                            remainingSeconds = timerRemaining,
                            elapsedSeconds = timerElapsed,
                            isRunning = isTimerRunning,
                            companionMessage = companionMessage,
                            onPause = { reason -> viewModel.pauseTimer(screen.taskId, reason) },
                            onResume = { viewModel.resumeTimer(screen.taskId) },
                            onComplete = { reflection -> viewModel.completeTaskFromTimer(screen.taskId, reflection) },
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.Notes -> {
                        NotesScreen(
                            notes = allNotes,
                            tasks = activeTasks + completedTasks,
                            onAddNote = { taskId, content, mood -> viewModel.addNote(taskId, content, mood) },
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.History -> {
                        TaskHistoryScreen(
                            completedTasks = completedTasks,
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.LifeGrowth -> {
                        LifeGrowthScreen(
                            latestSnapshot = latestSnapshot,
                            history = growthHistory,
                            currentLevel = currentRpgLevel,
                            unlockedMilestones = unlockedMilestones,
                            activeStreakDays = activeStreakDays,
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            currentSupabaseUrl = viewModel.supabaseUrl,
                            currentSupabaseKey = viewModel.supabaseKey,
                            supabaseStatus = supabaseStatus,
                            onSaveSupabaseConfig = { url, key -> viewModel.updateSupabaseConfig(url, key) },
                            onTestSupabase = { viewModel.testSupabaseConnection() },
                            onReloadSeedData = { viewModel.reloadSeedData() },
                            testResults = testResults,
                            isRunningTests = isRunningTests,
                            onRun20Tests = { viewModel.run20E2ETests() },
                            onBack = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    else -> {
                        viewModel.navigateTo(Screen.Home)
                    }
                }
                }
                
                // Show celebration overlay on any screen if lastReward is present
                if (lastReward != null) {
                    com.example.ui.components.ConfettiOverlay()
                }
            }
        }
    }
}

@Composable
fun CosmicBottomNavigationBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                CosmicBorder.copy(alpha = 0.6f),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .testTag("cosmic_bottom_bar"),
        containerColor = CosmicSurface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Now",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("Now", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CosmicVoid,
                selectedTextColor = MangoWarm,
                indicatorColor = MangoWarm,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Actions,
            onClick = { onNavigate(Screen.Actions) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Actions",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("Actions", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CosmicVoid,
                selectedTextColor = MangoWarm,
                indicatorColor = MangoWarm,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.LifeGrowth,
            onClick = { onNavigate(Screen.LifeGrowth) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Journey",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("Journey", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CosmicVoid,
                selectedTextColor = MangoWarm,
                indicatorColor = MangoWarm,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Notes,
            onClick = { onNavigate(Screen.Notes) },
            icon = {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = "Notes",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("Notes", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CosmicVoid,
                selectedTextColor = MangoWarm,
                indicatorColor = MangoWarm,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Settings,
            onClick = { onNavigate(Screen.Settings) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CosmicVoid,
                selectedTextColor = MangoWarm,
                indicatorColor = MangoWarm,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            )
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
