package com.shopnobilash.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Brand palette ─────────────────────────────────────────────────────────────
val Primary         = Color(0xFF00A74B)
// Aliases for screens that still reference old names
val Accent          = Primary
val AccentDeep      = Color(0xFF006E2F)
val AccentSoft      = Color(0xFFE6F7EE)
val AccentSoft2     = Color(0xFFCCF0DC)
val Ink             = Color(0xFF151617)
val Ink2            = Color(0xFF4D4D4D)
val Muted           = Color(0xFF7A7C7D)
val Faint           = Color(0xFFC5C7C8)
val LineLight       = Color(0xFFE0E1E2)
val Line2           = Color(0xFFD0D1D2)
val Bg              = Color(0xFFF0F1F2)
val CardWhite       = Color(0xFFFFFFFF)
val FieldBg         = Color(0xFFE8E9EA)
val PrimaryDeep     = Color(0xFF006E2F)
val PrimaryLight    = Color(0xFFE6F7EE)
val PrimaryLight2   = Color(0xFFCCF0DC)
val PrimaryDark     = Color(0xFF003D1A)
val PrimaryDark2    = Color(0xFF002912)

val TagOrange       = Color(0xFFE27A38)
val TagSoft         = Color(0xFFFBEEE2)
val TagSoftDark     = Color(0xFF3D2210)

val StarYellow      = Color(0xFFF4A92C)
val Danger          = Color(0xFFE5564B)
val Blue            = Color(0xFF2F6BE3)

// ── Light raw values ──────────────────────────────────────────────────────────
val LightBg         = Color(0xFFF0F1F2)
val LightCard       = Color(0xFFFFFFFF)
val LightField      = Color(0xFFE8E9EA)
val LightInk        = Color(0xFF151617)
val LightInk2       = Color(0xFF4D4D4D)
val LightMuted      = Color(0xFF7A7C7D)
val LightFaint      = Color(0xFFC5C7C8)
val LightLine       = Color(0xFFE0E1E2)
val LightLine2      = Color(0xFFD0D1D2)

// ── Dark raw values ───────────────────────────────────────────────────────────
val DarkBg          = Color(0xFF000000)
val DarkCard        = Color(0xFF2E3132)
val DarkField       = Color(0xFF1A1C1D)
val DarkInk         = Color(0xFFFFFFFF)
val DarkInk2        = Color(0xFFC5C7C8)
val DarkMuted       = Color(0xFF8A8C8D)
val DarkFaint       = Color(0xFF4D4F50)
val DarkLine        = Color(0xFF3D4042)
val DarkLine2       = Color(0xFF2E3132)

// ── Extended app colors not in MD3 slots ──────────────────────────────────────
@Immutable
data class AppColors(
    val accent: Color      = Primary,
    val accentDeep: Color  = PrimaryDeep,
    val accentSoft: Color  = PrimaryLight,
    val accentSoft2: Color = PrimaryLight2,
    val tag: Color         = TagOrange,
    val tagSoft: Color     = TagSoft,
    val ink: Color         = LightInk,
    val ink2: Color        = LightInk2,
    val muted: Color       = LightMuted,
    val faint: Color       = LightFaint,
    val line: Color        = LightLine,
    val line2: Color       = LightLine2,
    val bg: Color          = LightBg,
    val card: Color        = LightCard,
    val field: Color       = LightField,
    val star: Color        = StarYellow,
    val danger: Color      = Danger,
    val blue: Color        = Blue,
)

val DarkAppColors = AppColors(
    accent      = Primary,
    accentDeep  = PrimaryDeep,
    accentSoft  = PrimaryDark,
    accentSoft2 = PrimaryDark2,
    tag         = TagOrange,
    tagSoft     = TagSoftDark,
    ink         = DarkInk,
    ink2        = DarkInk2,
    muted       = DarkMuted,
    faint       = DarkFaint,
    line        = DarkLine,
    line2       = DarkLine2,
    bg          = DarkBg,
    card        = DarkCard,
    field       = DarkField,
    star        = StarYellow,
    danger      = Danger,
    blue        = Blue,
)

val LocalAppColors = staticCompositionLocalOf { AppColors() }

// ── MD3 Color Schemes ─────────────────────────────────────────────────────────
val LightColorScheme = lightColorScheme(
    primary              = Primary,
    onPrimary            = Color.White,
    primaryContainer     = PrimaryLight,
    onPrimaryContainer   = PrimaryDeep,
    secondary            = PrimaryDeep,
    onSecondary          = Color.White,
    secondaryContainer   = PrimaryLight2,
    onSecondaryContainer = PrimaryDeep,
    tertiary             = TagOrange,
    onTertiary           = Color.White,
    tertiaryContainer    = TagSoft,
    onTertiaryContainer  = TagOrange,
    error                = Danger,
    onError              = Color.White,
    background           = LightBg,
    onBackground         = LightInk,
    surface              = LightCard,
    onSurface            = LightInk,
    surfaceVariant       = LightField,
    onSurfaceVariant     = LightInk2,
    outline              = LightLine2,
    outlineVariant       = LightLine,
)

val DarkColorScheme = darkColorScheme(
    primary              = Primary,
    onPrimary            = Color.White,
    primaryContainer     = PrimaryDeep,
    onPrimaryContainer   = PrimaryLight,
    secondary            = Primary,
    onSecondary          = Color.Black,
    secondaryContainer   = PrimaryDark,
    onSecondaryContainer = PrimaryLight,
    tertiary             = TagOrange,
    onTertiary           = Color.Black,
    tertiaryContainer    = TagSoftDark,
    onTertiaryContainer  = TagOrange,
    error                = Danger,
    onError              = Color.White,
    background           = DarkBg,
    onBackground         = DarkInk,
    surface              = DarkCard,
    onSurface            = DarkInk,
    surfaceVariant       = DarkField,
    onSurfaceVariant     = DarkInk2,
    outline              = DarkLine,
    outlineVariant       = DarkLine2,
)
