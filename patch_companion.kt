        // Companion Guidance
        if (companionMessage != null) {
            Surface(
                color = CosmicCard.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StarlightGold.copy(alpha = 0.3f)),
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Companion",
                        tint = StarlightGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = companionMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSerene,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Text(
                text = "“Whatever you are doing, just be involved with all your heart. Life responds to intensity, not calculation.”",
                style = MaterialTheme.typography.bodyMedium,
                color = TextCosmic.copy(alpha = 0.8f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
