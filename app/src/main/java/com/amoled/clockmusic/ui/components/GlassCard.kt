package com.amoled.clockmusic.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.amoled.clockmusic.ui.theme.*

// ─── Glass morphism card (Apple-style large corners) ─────────────────────────
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    showDotGrid: Boolean = true,       // Nothing-style dot texture
    borderAlpha: Float = 0.18f,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                color  = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                shape  = shape
            )
            // Top-left → bottom-right glass highlight (like curved glass)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GlassWhite10, Color.Transparent),
                    start  = Offset(0f, 0f),
                    end    = Offset(200f, 200f)
                ),
                shape = shape
            )
            // Circuit-trace border — Nothing aesthetic
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassBorder.copy(alpha = borderAlpha * 1.4f),
                        GlassBorder.copy(alpha = borderAlpha * 0.2f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            )
    ) {
        // Dot grid overlay (Nothing aesthetic) — only if requested
        if (showDotGrid) {
            DotGridOverlay(
                dotColor = FusionDotDim,
                modifier = Modifier.matchParentSize()
            )
        }
        content()
    }
}

// ─── Compact tile (song rows, day chips, etc.) ────────────────────────────────
@Composable
fun GlassTile(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    selected: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val baseColor = if (selected)
        MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.90f)
    else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)

    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(color = baseColor, shape = shape)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent
                        else FusionCircuit,
                shape = shape
            ),
        content = content
    )
}

// ─── Nothing dot-grid background overlay ─────────────────────────────────────
@Composable
fun DotGridOverlay(
    dotColor: Color = FusionDotDim,
    spacing: Float = 18f,
    dotRadius: Float = 1.2f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cols = (size.width  / spacing).toInt() + 1
        val rows = (size.height / spacing).toInt() + 1
        for (row in 0..rows) {
            for (col in 0..cols) {
                drawCircle(
                    color  = dotColor,
                    radius = dotRadius,
                    center = Offset(col * spacing, row * spacing)
                )
            }
        }
    }
}

// ─── Nothing Glyph-style ring (animated LED arc) ─────────────────────────────
/**
 * Circular ring of LED-style dots that animate like Nothing's Glyph Interface.
 * Used around the album art disc and clock faces.
 */
@Composable
fun GlyphRing(
    progress: Float,          // 0..1 — how much of the ring is "lit"
    color: Color = NothingRed,
    dimColor: Color = NothingRim,
    dotCount: Int = 60,
    modifier: Modifier = Modifier,
    pulse: Boolean = false    // adds breathing glow when active
) {
    val pulseAlpha by rememberInfiniteTransition(label = "glyphPulse").animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1.0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pa"
    )

    Canvas(modifier = modifier) {
        val cx     = size.width  / 2f
        val cy     = size.height / 2f
        val radius = (minOf(size.width, size.height) / 2f) - 8f
        val dotR   = (2f * Math.PI.toFloat() * radius / dotCount) * 0.3f

        for (i in 0 until dotCount) {
            val angle     = (i.toFloat() / dotCount) * 2f * Math.PI.toFloat() - Math.PI.toFloat() / 2f
            val x         = cx + radius * kotlin.math.cos(angle)
            val y         = cy + radius * kotlin.math.sin(angle)
            val lit       = i.toFloat() / dotCount <= progress
            val alpha     = if (lit && pulse) pulseAlpha else 1f
            drawCircle(
                color  = (if (lit) color else dimColor).copy(alpha = if (lit) alpha else 0.3f),
                radius = if (lit) dotR * 1.3f else dotR,
                center = Offset(x, y)
            )
        }
    }
}

// ─── Glyph strip — horizontal version (music progress bar) ───────────────────
@Composable
fun GlyphStrip(
    progress: Float,
    color: Color = NothingRed,
    dimColor: Color = NothingGray3,
    dotCount: Int = 60,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val step = size.width / dotCount
        val dotR = step * 0.28f
        for (i in 0 until dotCount) {
            val lit = i.toFloat() / dotCount < progress
            drawCircle(
                color  = if (lit) color else dimColor,
                radius = dotR,
                center = Offset(i * step + step / 2f, size.height / 2f)
            )
        }
    }
}

// ─── Nothing-style segmented label badge ("ACTIVE · 5M") ─────────────────────
@Composable
fun NothingBadge(
    text: String,
    modifier: Modifier = Modifier,
    active: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (active) NothingRed.copy(alpha = 0.15f)
                else        NothingSlate
            )
            .border(
                width = 1.dp,
                color = if (active) NothingRed.copy(alpha = 0.5f) else NothingRim,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text  = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (active) NothingRed else NothingGray1
        )
    }
}
