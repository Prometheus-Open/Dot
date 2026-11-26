package com.amoled.clockmusic.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// System default sans-serif (Roboto)
val DefaultSans  = FontFamily.Default
// Monospace — used for block digit clock (closest to NDot on stock Android)
val MonoFamily   = FontFamily.Monospace

val AppTypography = Typography(
    // ── Huge block-clock display (NDot style) ───────────────────────────────
    displayLarge = TextStyle(
        fontFamily    = MonoFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 96.sp,
        lineHeight    = 96.sp,
        letterSpacing = (-2).sp
    ),
    displayMedium = TextStyle(
        fontFamily    = MonoFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 64.sp,
        lineHeight    = 64.sp,
        letterSpacing = (-1).sp
    ),
    displaySmall = TextStyle(
        fontFamily    = MonoFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 48.sp,
        lineHeight    = 52.sp,
        letterSpacing = 0.sp
    ),

    // ── Headings ─────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 24.sp,
        lineHeight = 32.sp
    ),

    // ── Titles ───────────────────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily    = DefaultSans,
        fontWeight    = FontWeight.Medium,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily    = DefaultSans,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ── Body ─────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = DefaultSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),

    // ── Labels (NDot-inspired: ALL CAPS + wide tracking) ─────────────────────
    labelLarge = TextStyle(
        fontFamily    = MonoFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 13.sp,
        lineHeight    = 20.sp,
        letterSpacing = 2.sp    // wide tracking = Nothing feel
    ),
    labelMedium = TextStyle(
        fontFamily    = MonoFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 1.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = MonoFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        letterSpacing = 1.sp
    ),
)
