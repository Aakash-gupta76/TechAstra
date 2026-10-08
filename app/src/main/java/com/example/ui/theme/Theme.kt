package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TechAstraColorScheme = darkColorScheme(
    primary = TechAstraGold,
    onPrimary = Color(0xFF1E1400),
    primaryContainer = Color(0xFF453000),
    onPrimaryContainer = TechAstraGoldLight,

    secondary = AccentCyan,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5B),
    onSecondaryContainer = Color(0xFFA5EEFF),

    tertiary = AccentPurple,
    onTertiary = Color(0xFF2C0B60),
    tertiaryContainer = Color(0xFF441B88),
    onTertiaryContainer = Color(0xFFE9DDFF),

    background = TechBackground,
    onBackground = TechTextPrimary,
    surface = TechSurface,
    onSurface = TechTextPrimary,
    surfaceVariant = TechSurfaceVariant,
    onSurfaceVariant = TechTextSecondary,

    outline = TechCardBorder,
    outlineVariant = Color(0xFF26334D),

    error = AccentRed,
    onError = Color.White
)

@Composable
fun TechAstraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TechAstraColorScheme,
        typography = Typography,
        content = content
    )
}
