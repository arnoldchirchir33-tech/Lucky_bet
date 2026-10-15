package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuckyDarkColorScheme = darkColorScheme(
    primary = LuckyGreen,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = LuckyGreenBright,
    secondary = LuckyCrimson,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C0519),
    onSecondaryContainer = LuckyCrimsonLight,
    tertiary = LuckyGold,
    onTertiary = Color(0xFF1E1B18),
    background = LuckyBgDark,
    onBackground = LuckyTextPrimary,
    surface = LuckySurfaceDark,
    onSurface = LuckyTextPrimary,
    surfaceVariant = LuckySurfaceElevated,
    onSurfaceVariant = LuckyTextSecondary,
    outline = LuckyBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LuckyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
