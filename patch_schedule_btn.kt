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
                        Text("Schedule", color = StarlightGold, fontWeight = FontWeight.Bold)
                    }
