package com.dorent.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Brand palette ────────────────────────────────────────────────────────────
val Accent        = Color(0xFF1FAE84)
val AccentDeep    = Color(0xFF17916C)
val AccentSoft    = Color(0xFFE6F7F0)
val AccentSoft2   = Color(0xFFD6F1E6)
val TagOrange     = Color(0xFFE27A38)
val TagSoft       = Color(0xFFFBEEE2)
val Ink           = Color(0xFF16191C)
val Ink2          = Color(0xFF3B4045)
val Muted         = Color(0xFF7C838B)
val Faint         = Color(0xFFAAB0B6)
val LineLight     = Color(0xFFEEF1F3)
val Line2         = Color(0xFFE3E7EA)
val Bg            = Color(0xFFF4F6F8)
val CardWhite     = Color(0xFFFFFFFF)
val FieldBg       = Color(0xFFF0F2F5)
val StarYellow    = Color(0xFFF4A92C)
val Danger        = Color(0xFFE5564B)
val Blue          = Color(0xFF2F6BE3)

// ── Extended app colors not in MD3 slots ─────────────────────────────────────
@Immutable
data class AppColors(
    val accent: Color = Accent,
    val accentDeep: Color = AccentDeep,
    val accentSoft: Color = AccentSoft,
    val accentSoft2: Color = AccentSoft2,
    val tag: Color = TagOrange,
    val tagSoft: Color = TagSoft,
    val ink: Color = Ink,
    val ink2: Color = Ink2,
    val muted: Color = Muted,
    val faint: Color = Faint,
    val line: Color = LineLight,
    val line2: Color = Line2,
    val bg: Color = Bg,
    val card: Color = CardWhite,
    val field: Color = FieldBg,
    val star: Color = StarYellow,
    val danger: Color = Danger,
    val blue: Color = Blue,
)

val LocalAppColors = staticCompositionLocalOf { AppColors() }

// ── MD3 Color Schemes ─────────────────────────────────────────────────────────
val LightColorScheme = lightColorScheme(
    primary              = Accent,
    onPrimary            = Color.White,
    primaryContainer     = AccentSoft,
    onPrimaryContainer   = AccentDeep,
    secondary            = Blue,
    onSecondary          = Color.White,
    secondaryContainer   = AccentSoft2,
    onSecondaryContainer = AccentDeep,
    tertiary             = TagOrange,
    onTertiary           = Color.White,
    tertiaryContainer    = TagSoft,
    onTertiaryContainer  = TagOrange,
    error                = Danger,
    onError              = Color.White,
    background           = Bg,
    onBackground         = Ink,
    surface              = CardWhite,
    onSurface            = Ink,
    surfaceVariant       = FieldBg,
    onSurfaceVariant     = Muted,
    outline              = Line2,
    outlineVariant       = LineLight,
)

val DarkColorScheme = darkColorScheme(
    primary              = Accent,
    onPrimary            = Color.White,
    primaryContainer     = AccentDeep,
    onPrimaryContainer   = AccentSoft,
    secondary            = Blue,
    onSecondary          = Color.White,
    background           = Color(0xFF0E1117),
    onBackground         = Color(0xFFE2E6EA),
    surface              = Color(0xFF171B22),
    onSurface            = Color(0xFFE2E6EA),
    surfaceVariant       = Color(0xFF1F252D),
    onSurfaceVariant     = Muted,
    outline              = Color(0xFF2B3140),
)
