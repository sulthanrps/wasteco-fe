package com.example.wasteco.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private fun WasteCoPalette.toLightScheme(): ColorScheme = lightColorScheme(
    primary = primaryMain,
    onPrimary = OnSolid,
    primaryContainer = primaryContainer,
    onPrimaryContainer = primaryDarker,
    secondary = secondaryMain,
    onSecondary = OnSolid,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = secondaryDark,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = textSecondary,
    outline = outline,
    outlineVariant = outlineVariant,
    error = errorRed,
    onError = OnSolid,
    errorContainer = errorContainer,
    onErrorContainer = errorRed
)

private fun WasteCoPalette.toDarkScheme(): ColorScheme = darkColorScheme(
    primary = primaryMain,
    onPrimary = OnSolid,
    primaryContainer = primaryContainer,
    onPrimaryContainer = primaryDarker,
    secondary = secondaryMain,
    onSecondary = OnSolid,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = secondaryDark,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = textSecondary,
    surfaceContainerLowest = background,
    surfaceContainerLow = surface,
    surfaceContainer = surface,
    surfaceContainerHigh = surfaceVariant,
    surfaceContainerHighest = surfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    error = errorRed,
    onError = background,
    errorContainer = errorContainer,
    onErrorContainer = errorRed
)

private val LightColorScheme = LightPalette.toLightScheme()
private val DarkColorScheme = DarkPalette.toDarkScheme()

@Composable
fun WasteCoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalWasteCoPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
