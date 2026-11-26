package com.amoled.clockmusic.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── 1. NOTHING Theme — pure black, red accent, monochrome ───────────────────
val NothingColorScheme = darkColorScheme(
    primary            = NothingRed,
    onPrimary          = NothingWhite,
    primaryContainer   = NothingRedDim,
    onPrimaryContainer = NothingRed,

    secondary          = NothingSteel,
    onSecondary        = NothingWhite,
    secondaryContainer = NothingSlate,
    onSecondaryContainer = NothingGray1,

    tertiary           = NothingGray1,
    onTertiary         = NothingBlack,
    tertiaryContainer  = NothingRim,
    onTertiaryContainer= NothingWhite,

    background         = NothingBlack,      // pure OLED black
    onBackground       = NothingWhite,

    surface            = NothingCharcoal,
    onSurface          = NothingWhite,
    surfaceVariant     = NothingSlate,
    onSurfaceVariant   = NothingGray1,

    outline            = NothingRim,
    outlineVariant     = NothingSteel,

    error              = Color(0xFFE57373),
    onError            = NothingBlack,
    errorContainer     = Color(0xFF3A0A0A),
    onErrorContainer   = Color(0xFFE57373),

    inverseSurface     = NothingWhite,
    inverseOnSurface   = NothingBlack,
    inversePrimary     = NothingRedDark,

    scrim              = Color(0xCC000000),
    surfaceTint        = NothingRed,
)

// ─── 2. BLOCKIT + NOTHING Fusion (Warm brown + dot matrix / red accent) ───────
val FusionColorScheme = darkColorScheme(
    primary            = NothingRed,            // Nothing Red replaces terracotta for active states
    onPrimary          = CreamWhite,
    primaryContainer   = NothingRedDim,
    onPrimaryContainer = NothingRed,

    secondary          = ElevatedBrown,
    onSecondary        = CreamWhite,
    secondaryContainer = SurfaceBrown,
    onSecondaryContainer = CreamMid,

    tertiary           = Terracotta,            // terracotta still lives as tertiary warmth
    onTertiary         = DeepBrown,
    tertiaryContainer  = TerracottaDark,
    onTertiaryContainer= CreamWhite,

    background         = WarmBlack,             // warm AMOLED (not pure #000)
    onBackground       = CreamWhite,

    surface            = DeepBrown,
    onSurface          = CreamWhite,
    surfaceVariant     = SurfaceBrown,
    onSurfaceVariant   = CreamMid,

    outline            = RimBrown,
    outlineVariant     = ElevatedBrown,

    error              = Color(0xFFE57373),
    onError            = WarmBlack,
    errorContainer     = Color(0xFF4A1010),
    onErrorContainer   = Color(0xFFE57373),

    inverseSurface     = CreamWhite,
    inverseOnSurface   = DeepBrown,
    inversePrimary     = NothingRedDark,

    scrim              = Color(0xCC000000),
    surfaceTint        = NothingRed,
)

// ─── 3. AMOLED Warm (original Blockit style) ──────────────────────────────────
val AmoledDarkColorScheme = darkColorScheme(
    primary            = Terracotta,
    onPrimary          = CreamWhite,
    primaryContainer   = TerracottaDim,
    onPrimaryContainer = TerracottaLight,
    secondary          = ElevatedBrown,
    onSecondary        = CreamWhite,
    secondaryContainer = SurfaceBrown,
    onSecondaryContainer = CreamMid,
    background         = WarmBlack,
    onBackground       = CreamWhite,
    surface            = DeepBrown,
    onSurface          = CreamWhite,
    surfaceVariant     = SurfaceBrown,
    onSurfaceVariant   = CreamMid,
    outline            = RimBrown,
    outlineVariant     = ElevatedBrown,
    error              = Color(0xFFE57373),
    onError            = WarmBlack,
    errorContainer     = Color(0xFF4A1010),
    onErrorContainer   = Color(0xFFE57373),
    inverseSurface     = CreamWhite,
    inverseOnSurface   = DeepBrown,
    inversePrimary     = TerracottaDark,
    scrim              = Color(0xCC000000),
    surfaceTint        = Terracotta,
)

/** Selectable app themes. */
enum class AppTheme {
    BLOCKIT,        // warm Blockit brown + terracotta
    NOTHING,        // pure Nothing — black + red
    FUSION,         // Blockit layout + Nothing dot aesthetic + red accent
}

@Composable
fun ClockMusicTheme(
    appTheme: AppTheme = AppTheme.FUSION,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.NOTHING -> NothingColorScheme
        AppTheme.FUSION  -> FusionColorScheme
        AppTheme.BLOCKIT -> AmoledDarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val ctrl = WindowCompat.getInsetsController(window, view)
            ctrl.isAppearanceLightStatusBars     = false
            ctrl.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
