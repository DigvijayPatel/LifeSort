import re

with open('app/src/main/java/com/example/ui/screens/TaskDetailScreen.kt', 'r') as f:
    content = f.read()

replacement = """        // Bottom Action Controls
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
                                imageVector = androidx.compose.material.icons.filled.Schedule,
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
        }"""

content = re.sub(r'        // Bottom Action Controls\s*item \{\s*Spacer\(modifier = Modifier\.height\(10\.dp\)\)\s*Row\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*horizontalArrangement = Arrangement\.spacedBy\(12\.dp\)\s*\) \{.*?\}\s*\}\s*\}', replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/TaskDetailScreen.kt', 'w') as f:
    f.write(content)
