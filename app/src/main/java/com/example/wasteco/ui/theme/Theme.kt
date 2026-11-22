package com.example.wasteco.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryMain,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryDarker,

    secondary = SecondaryMain,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = SecondaryDark,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Outline,
    outlineVariant = OutlineVariant,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryMain,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,

    secondary = SecondaryMain,
    onSecondary = Color.White,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = SecondaryLight,

    background = Color(0xFF14131B),
    onBackground = Color(0xFFF7F7FC),

    surface = Color(0xFF1E1D27),
    onSurface = Color(0xFFF7F7FC),
    surfaceVariant = Color(0xFF282733),
    onSurfaceVariant = Color(0xFFA0A3BD),

    outline = Color(0xFF3F3E4D),
    outlineVariant = Color(0xFF2E2D3A),

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun WasteCoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
