package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.TestCaseResult
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicCard
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.CosmicVoid
import com.example.ui.theme.MangoWarm
import com.example.ui.theme.NebulaPink
import com.example.ui.theme.SacredEmerald
import com.example.ui.theme.StarlightGold
import com.example.ui.theme.TextCosmic
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPure
import com.example.ui.theme.TextSerene

@Composable
fun SettingsScreen(
    currentSupabaseUrl: String,
    currentSupabaseKey: String,
    supabaseStatus: String,
    onSaveSupabaseConfig: (String, String) -> Unit,
    onTestSupabase: () -> Unit,
    onReloadSeedData: () -> Unit,
    testResults: List<TestCaseResult>,
    isRunningTests: Boolean,
    onRun20Tests: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var urlInput by remember { mutableStateOf(currentSupabaseUrl) }
    var keyInput by remember { mutableStateOf(currentSupabaseKey) }
    var clipboardMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
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
                        text = "SETTINGS & SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = MangoWarm,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Cloud Backup & Preferences",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPure
                    )
                }
            }
        }

        // Feedback toast for clipboard
        if (clipboardMessage != null) {
            item {
                Surface(
                    color = MangoWarm.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MangoWarm.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MangoWarm,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = clipboardMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MangoWarm,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Supabase Cloud Persistence Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Supabase",
                            tint = MangoWarm,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUPABASE CLOUD SYNC",
                            style = MaterialTheme.typography.labelSmall,
                            color = MangoWarm,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Backup and sync your tasks, reflections, and growth history across your devices.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Supabase URL with Copy/Paste options
                    Text(
                        text = "Project URL",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        placeholder = { Text("https://xyz.supabase.co", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = MangoWarm,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    urlInput = clip.trim()
                                    clipboardMessage = "Pasted URL from clipboard"
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp), tint = MangoWarm)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste URL", fontSize = 11.sp, color = MangoWarm)
                        }

                        if (urlInput.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(urlInput))
                                    clipboardMessage = "Copied URL to clipboard"
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSerene)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy", fontSize = 11.sp, color = TextSerene)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Supabase Anon Key with Copy/Paste options
                    Text(
                        text = "Supabase Anon Key",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        placeholder = { Text("eyJhbGciOi...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("supabase_anon_key_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPure,
                            unfocusedTextColor = TextPure,
                            focusedBorderColor = MangoWarm,
                            unfocusedBorderColor = CosmicBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    keyInput = clip.trim()
                                    clipboardMessage = "Pasted Anon Key from clipboard"
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("paste_anon_key_button")
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp), tint = MangoWarm)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste Anon Key", fontSize = 11.sp, color = MangoWarm, fontWeight = FontWeight.SemiBold)
                        }

                        if (keyInput.isNotBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(keyInput))
                                    clipboardMessage = "Copied Anon Key to clipboard"
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_anon_key_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSerene)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Key", fontSize = 11.sp, color = TextSerene)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveSupabaseConfig(urlInput, keyInput)
                                clipboardMessage = "Configuration saved!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MangoWarm),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("save_supabase_config_button")
                        ) {
                            Text("Save Config", color = CosmicVoid, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onTestSupabase,
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test Connection", color = TextPure)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = CosmicSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Status: $supabaseStatus",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextCosmic,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Gemini AI Engine Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "AI BRAIN DUMP ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gemini 3.5 Flash (with offline deterministic fallback)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPure
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Transforms raw unorganized thoughts into clear, structured tasks with calm sub-steps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSerene
                    )
                }
            }
        }

        // Seed Realistic Data Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SAMPLE TASKS & RESTORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextCosmic,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reload clean sample tasks, reflections, and 7-day progress history.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onReloadSeedData()
                            clipboardMessage = "Sample tasks reloaded"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = MangoWarm,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reload Sample Tasks & History", color = MangoWarm, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // 20 END-TO-END VERIFICATION TEST SUITE
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verification_test_suite_card"),
                colors = CardDefaults.cardColors(containerColor = CosmicCard),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MangoWarm.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Testing",
                                tint = MangoWarm,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM VERIFICATION (20 TESTS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MangoWarm,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        if (testResults.isNotEmpty()) {
                            val passedCount = testResults.count { it.passed }
                            Surface(
                                color = SacredEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "$passedCount / 20 PASS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SacredEmerald,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Automated tests that verify task priorities, timers, pause/resume, notes, and life score accuracy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onRun20Tests,
                        enabled = !isRunningTests,
                        colors = ButtonDefaults.buttonColors(containerColor = MangoWarm),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("run_20_tests_button")
                    ) {
                        if (isRunningTests) {
                            CircularProgressIndicator(
                                color = CosmicVoid,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Running Tests...", color = CosmicVoid, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run",
                                tint = CosmicVoid
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (testResults.isEmpty()) "Run System Tests" else "Re-Run All 20 Tests",
                                color = CosmicVoid,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Test Results Items
        if (testResults.isNotEmpty()) {
            item {
                Text(
                    text = "TEST AUDIT REPORT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCosmic,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(testResults) { test ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (test.passed) SacredEmerald.copy(alpha = 0.4f) else NebulaPink.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (test.passed) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = if (test.passed) "Passed" else "Failed",
                            tint = if (test.passed) SacredEmerald else NebulaPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Test #${test.testNumber}: ${test.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPure,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = test.details,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSerene,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
