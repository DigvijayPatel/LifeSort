package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecycleBinScreen(
    deletedTasks: List<TaskEntity>,
    onRestoreTask: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Only show tasks deleted within the last 30 days
    val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000
    val currentTime = System.currentTimeMillis()
    val recentDeletedTasks = deletedTasks.filter {
        (currentTime - it.updatedAt) <= thirtyDaysInMillis
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("recycle_bin_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        text = "✦ RECYCLE BIN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MangoWarm,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Restore Intentions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPure
                    )
                }
            }
        }

        if (recentDeletedTasks.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Recycle bin is empty.", color = TextMuted)
                }
            }
        } else {
            items(recentDeletedTasks, key = { it.id }) { task ->
                DeletedTaskCard(task = task, onRestore = { onRestoreTask(task.id) })
            }
        }
    }
}

@Composable
fun DeletedTaskCard(
    task: TaskEntity,
    onRestore: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextMuted,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deleted: ${dateFormat.format(Date(task.updatedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSerene
                )
            }
            IconButton(
                onClick = onRestore,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Restore",
                    tint = SacredEmerald
                )
            }
        }
    }
}
