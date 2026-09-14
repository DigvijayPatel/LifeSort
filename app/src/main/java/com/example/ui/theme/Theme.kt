package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CosmicColorScheme = darkColorScheme(
    primary = MangoWarm,
    onPrimary = CosmicVoid,
    primaryContainer = CelestialViolet,
    onPrimaryContainer = TextPure,
    secondary = MysticCyan,
    onSecondary = CosmicVoid,
    secondaryContainer = CosmicSurfaceVariant,
    onSecondaryContainer = TextSerene,
    tertiary = EtherealPurple,
    onTertiary = CosmicVoid,
    background = CosmicVoid,
    onBackground = TextPure,
    surface = CosmicSurface,
    onSurface = TextPure,
    surfaceVariant = CosmicCard,
    onSurfaceVariant = TextSerene,
    outline = CosmicBorder,
    outlineVariant = Color(0xFF383060)
)

@Composable
fun LifeSortTheme(
    darkTheme: Boolean = true, // Cosmic immersive is intentionally dark
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CosmicColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) = LifeSortTheme(darkTheme = darkTheme, content = content)

