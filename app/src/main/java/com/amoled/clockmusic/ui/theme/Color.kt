package com.amoled.clockmusic.ui.theme

import androidx.compose.ui.graphics.Color

// ─── BLOCKIT Palette (warm brown foundation) ──────────────────────────────────
val WarmBlack      = Color(0xFF0D0B0A)   // AMOLED warm near-black
val DeepBrown      = Color(0xFF1A1613)   // primary dark background
val SurfaceBrown   = Color(0xFF231F1B)   // card/surface
val ElevatedBrown  = Color(0xFF2E2825)   // elevated card
val RimBrown       = Color(0xFF3D3630)   // outline / rim

// Warm Cream / foreground text
val CreamWhite     = Color(0xFFF5EDE3)   // primary text
val CreamMid       = Color(0xFFBFAFA0)   // secondary text
val CreamDim       = Color(0xFF7A6D63)   // tertiary / hint

// Blockit accent — terracotta orange
val Terracotta     = Color(0xFFD4714A)
val TerracottaLight= Color(0xFFE8A07A)
val TerracottaDark = Color(0xFFAA4E2C)
val TerracottaDim  = Color(0xFF4A2010)

// ─── NOTHING Palette ──────────────────────────────────────────────────────────
// Pure monochrome base
val NothingBlack   = Color(0xFF000000)   // pure OLED black
val NothingCharcoal= Color(0xFF0F0F0F)   // deep surface
val NothingSlate   = Color(0xFF1A1A1A)   // card surface
val NothingSteel   = Color(0xFF2A2A2A)   // elevated surface
val NothingRim     = Color(0xFF3A3A3A)   // outline / circuit trace
val NothingGrid    = Color(0xFF242424)   // dot grid background

// Nothing whites
val NothingWhite   = Color(0xFFFFFFFF)   // primary text
val NothingGray1   = Color(0xFFB8B8B8)   // secondary text
val NothingGray2   = Color(0xFF707070)   // tertiary / dim
val NothingGray3   = Color(0xFF404040)   // faint / hint

// Nothing Red (the signature accent — used sparingly like a physical LED)
val NothingRed     = Color(0xFFFF3C3C)   // primary accent
val NothingRedDark = Color(0xFF8B0000)   // container / dim
val NothingRedDim  = Color(0xFF3A0A0A)   // background tint

// ─── Fusion palette (Blockit warm + Nothing cold) ─────────────────────────────
// Used in hybrid mode: warm surfaces, Nothing-style overlays
val FusionDotDim   = Color(0x14FFFFFF)   // dot grid dots (barely visible)
val FusionGlow     = Color(0x33FF3C3C)   // glyph red glow
val FusionCircuit  = Color(0x22FFFFFF)   // circuit trace border

// Glass morphism overlays
val GlassWhite10   = Color(0x1AFFFFFF)
val GlassWhite06   = Color(0x0FFFFFFF)
val GlassBorder    = Color(0x33FFFFFF)

// Selection highlight
val SelectedCard   = Color(0xFFF5EDE3)   // cream selected (Blockit)
