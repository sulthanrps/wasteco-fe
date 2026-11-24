package com.example.wasteco.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Theme-aware color palette. Every token below resolves against the palette provided by
 * [WasteCoTheme], so the same token reads well in both light and dark mode.
 *
 * Naming convention:
 * - `*Solid` tokens are filled backgrounds that carry white content; they stay saturated in dark mode.
 * - Ink / text tokens (e.g. [PrimaryDarker], [SecondaryDark]) lighten in dark mode.
 * - Tint / container tokens darken in dark mode.
 */
@Immutable
data class WasteCoPalette(
    val primaryMain: Color,
    val primaryDark: Color,
    val primaryDarker: Color,
    val primaryLight: Color,
    val primaryLighter: Color,
    val primaryContainer: Color,
    val brandSolid: Color,
    val secondaryMain: Color,
    val secondaryDark: Color,
    val secondaryLight: Color,
    val secondaryContainer: Color,
    val secondarySolid: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceMuted: Color,
    val surfaceSubtle: Color,
    val outline: Color,
    val outlineVariant: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val errorRed: Color,
    val errorContainer: Color,
    val warningYellow: Color,
    val warningContainer: Color,
    val warningTint: Color,
    val warningInk: Color,
    val infoBlue: Color,
    val infoContainer: Color,
    val successTint: Color,
    val logoBackdrop: Color,
    val entry: EntryPalette
)

@Immutable
data class EntryPalette(
    val background: Color,
    val surface: Color,
    val heading: Color,
    val body: Color,
    val muted: Color,
    val action: Color,
    val actionSolid: Color,
    val greenTint: Color,
    val oliveTint: Color,
    val orangeTint: Color,
    val orangeInk: Color,
    val warningInk: Color
)

internal val LightPalette = WasteCoPalette(
    primaryMain = Color(0xFF3E8E41),
    primaryDark = Color(0xFF2F6B31),
    primaryDarker = Color(0xFF1C401D),
    primaryLight = Color(0xFFECF4EC),
    primaryLighter = Color(0xFFE2EEE3),
    primaryContainer = Color(0xFFECF4EC),
    brandSolid = Color(0xFF1C401D),
    secondaryMain = Color(0xFFE87A24),
    secondaryDark = Color(0xFFBA621D),
    secondaryLight = Color(0xFFFDF2E9),
    secondaryContainer = Color(0xFFFCEBDE),
    secondarySolid = Color(0xFFBA621D),
    background = Color(0xFFF7F7FC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF0F1F0),
    surfaceMuted = Color(0xFFF7FAF7),
    surfaceSubtle = Color(0xFFF1F4F1),
    outline = Color(0xFFCFD4D0),
    outlineVariant = Color(0xFFE8EAE8),
    borderSubtle = Color(0xFFE5E7EB),
    textPrimary = Color(0xFF0D0B37),
    textSecondary = Color(0xFF6E7191),
    textTertiary = Color(0xFF8E98A8),
    errorRed = Color(0xFFDC2626),
    errorContainer = Color(0xFFFCE9E9),
    warningYellow = Color(0xFFF59E0B),
    warningContainer = Color(0xFFFEF5E7),
    warningTint = Color(0xFFFFF3E0),
    warningInk = Color(0xFFE65100),
    infoBlue = Color(0xFF1976D2),
    infoContainer = Color(0xFFE7F6FD),
    successTint = Color(0xFFE8F5E9),
    logoBackdrop = Color.Transparent,
    entry = EntryPalette(
        background = Color(0xFFEFF1EE),
        surface = Color(0xFFFCFDFC),
        heading = Color(0xFF242D27),
        body = Color(0xFF4D5B51),
        muted = Color(0xFF596E60),
        action = Color(0xFF306E32),
        actionSolid = Color(0xFF306E32),
        greenTint = Color(0xFFE9F1E8),
        oliveTint = Color(0xFFE5EBDD),
        orangeTint = Color(0xFFFBF0E5),
        orangeInk = Color(0xFF8F4908),
        warningInk = Color(0xFF76500D)
    )
)

internal val DarkPalette = WasteCoPalette(
    primaryMain = Color(0xFF4A9F4E),
    primaryDark = Color(0xFF8FD192),
    primaryDarker = Color(0xFFA8DCAA),
    primaryLight = Color(0xFF1F3322),
    primaryLighter = Color(0xFF243A27),
    primaryContainer = Color(0xFF1F3322),
    brandSolid = Color(0xFF2F6B31),
    secondaryMain = Color(0xFFE87A24),
    secondaryDark = Color(0xFFFFB57A),
    secondaryLight = Color(0xFF3A2819),
    secondaryContainer = Color(0xFF3D2A1A),
    secondarySolid = Color(0xFFBA621D),
    background = Color(0xFF111512),
    surface = Color(0xFF1A1F1B),
    surfaceVariant = Color(0xFF242A25),
    surfaceMuted = Color(0xFF212722),
    surfaceSubtle = Color(0xFF262D27),
    outline = Color(0xFF4A544C),
    outlineVariant = Color(0xFF2E352F),
    borderSubtle = Color(0xFF343B35),
    textPrimary = Color(0xFFECEFEC),
    textSecondary = Color(0xFFB4BDB6),
    textTertiary = Color(0xFF8C978E),
    errorRed = Color(0xFFFF8A80),
    errorContainer = Color(0xFF3E1F1F),
    warningYellow = Color(0xFFFBBF24),
    warningContainer = Color(0xFF3A2E17),
    warningTint = Color(0xFF3A2C1A),
    warningInk = Color(0xFFFFB074),
    infoBlue = Color(0xFF64B5F6),
    infoContainer = Color(0xFF16303D),
    successTint = Color(0xFF1F3322),
    logoBackdrop = Color(0xFFF4F7F3),
    entry = EntryPalette(
        background = Color(0xFF111512),
        surface = Color(0xFF1A1F1B),
        heading = Color(0xFFECEFEC),
        body = Color(0xFFC3CCC5),
        muted = Color(0xFFA3B0A6),
        action = Color(0xFF7CC47F),
        actionSolid = Color(0xFF306E32),
        greenTint = Color(0xFF1F3322),
        oliveTint = Color(0xFF283022),
        orangeTint = Color(0xFF3A2A1C),
        orangeInk = Color(0xFFFFB880),
        warningInk = Color(0xFFF2C46B)
    )
)

val LocalWasteCoPalette = staticCompositionLocalOf { LightPalette }

private val palette: WasteCoPalette
    @Composable @ReadOnlyComposable get() = LocalWasteCoPalette.current

// Primary Brand Colors (Green)
val PrimaryMain: Color @Composable @ReadOnlyComposable get() = palette.primaryMain
val PrimaryDark: Color @Composable @ReadOnlyComposable get() = palette.primaryDark
/** Dark green ink for text/icons. Use [BrandSolid] for filled backgrounds. */
val PrimaryDarker: Color @Composable @ReadOnlyComposable get() = palette.primaryDarker
val PrimaryLight: Color @Composable @ReadOnlyComposable get() = palette.primaryLight
val PrimaryLighter: Color @Composable @ReadOnlyComposable get() = palette.primaryLighter
val PrimaryContainer: Color @Composable @ReadOnlyComposable get() = palette.primaryContainer
/** Filled brand background (buttons, FABs, badges) that carries [OnSolid] content. */
val BrandSolid: Color @Composable @ReadOnlyComposable get() = palette.brandSolid

// Secondary Accent Colors (Orange)
val SecondaryMain: Color @Composable @ReadOnlyComposable get() = palette.secondaryMain
/** Dark orange ink for text/icons. Use [SecondarySolid] for filled backgrounds. */
val SecondaryDark: Color @Composable @ReadOnlyComposable get() = palette.secondaryDark
val SecondaryLight: Color @Composable @ReadOnlyComposable get() = palette.secondaryLight
val SecondaryContainer: Color @Composable @ReadOnlyComposable get() = palette.secondaryContainer
val SecondarySolid: Color @Composable @ReadOnlyComposable get() = palette.secondarySolid

// Neutral & Background Colors
val Background: Color @Composable @ReadOnlyComposable get() = palette.background
val Surface: Color @Composable @ReadOnlyComposable get() = palette.surface
val SurfaceVariant: Color @Composable @ReadOnlyComposable get() = palette.surfaceVariant
val SurfaceMuted: Color @Composable @ReadOnlyComposable get() = palette.surfaceMuted
val SurfaceSubtle: Color @Composable @ReadOnlyComposable get() = palette.surfaceSubtle
val Outline: Color @Composable @ReadOnlyComposable get() = palette.outline
val OutlineVariant: Color @Composable @ReadOnlyComposable get() = palette.outlineVariant
val BorderSubtle: Color @Composable @ReadOnlyComposable get() = palette.borderSubtle

// Text Colors
val TextPrimary: Color @Composable @ReadOnlyComposable get() = palette.textPrimary
val TextSecondary: Color @Composable @ReadOnlyComposable get() = palette.textSecondary
val TextTertiary: Color @Composable @ReadOnlyComposable get() = palette.textTertiary

/** Content placed on solid / image backgrounds. Identical in both themes. */
val OnSolid = Color(0xFFFFFFFF)
val TextInverse = OnSolid

// Functional Colors
val ErrorRed: Color @Composable @ReadOnlyComposable get() = palette.errorRed
val ErrorContainer: Color @Composable @ReadOnlyComposable get() = palette.errorContainer
val WarningYellow: Color @Composable @ReadOnlyComposable get() = palette.warningYellow
val WarningContainer: Color @Composable @ReadOnlyComposable get() = palette.warningContainer
val WarningTint: Color @Composable @ReadOnlyComposable get() = palette.warningTint
val WarningInk: Color @Composable @ReadOnlyComposable get() = palette.warningInk
val InfoBlue: Color @Composable @ReadOnlyComposable get() = palette.infoBlue
val InfoContainer: Color @Composable @ReadOnlyComposable get() = palette.infoContainer
val SuccessGreen: Color @Composable @ReadOnlyComposable get() = palette.primaryMain
val SuccessContainer: Color @Composable @ReadOnlyComposable get() = palette.primaryContainer
val SuccessTint: Color @Composable @ReadOnlyComposable get() = palette.successTint
/** Backdrop behind the raster brand logo; transparent in light mode, light pill in dark mode. */
val LogoBackdrop: Color @Composable @ReadOnlyComposable get() = palette.logoBackdrop

// Scrims over photos/maps. Identical in both themes because content on them is always white.
val ImageScrim = Color(0x99000000)
val ImageScrimStrong = Color(0xDD1E1E1E)

object EntryColors {
    val background: Color @Composable @ReadOnlyComposable get() = palette.entry.background
    val surface: Color @Composable @ReadOnlyComposable get() = palette.entry.surface
    val heading: Color @Composable @ReadOnlyComposable get() = palette.entry.heading
    val body: Color @Composable @ReadOnlyComposable get() = palette.entry.body
    val muted: Color @Composable @ReadOnlyComposable get() = palette.entry.muted
    /** Action ink for links/text. Use [actionSolid] for filled buttons. */
    val action: Color @Composable @ReadOnlyComposable get() = palette.entry.action
    val actionSolid: Color @Composable @ReadOnlyComposable get() = palette.entry.actionSolid
    val greenTint: Color @Composable @ReadOnlyComposable get() = palette.entry.greenTint
    val oliveTint: Color @Composable @ReadOnlyComposable get() = palette.entry.oliveTint
    val orangeTint: Color @Composable @ReadOnlyComposable get() = palette.entry.orangeTint
    val orangeInk: Color @Composable @ReadOnlyComposable get() = palette.entry.orangeInk
    val warningInk: Color @Composable @ReadOnlyComposable get() = palette.entry.warningInk
    val heroOverlay = Color(0xB3000000)
}
