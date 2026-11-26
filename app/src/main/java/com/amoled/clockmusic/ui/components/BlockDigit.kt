package com.amoled.clockmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * NDot-inspired dot-matrix digit renderer.
 *
 * Each digit is drawn as a 5×7 grid of circular dots — matching Nothing's NDot
 * typeface aesthetic: retro IBM terminal / LED matrix look.
 *
 * Active dots are lit in [color], inactive dots are shown in [dimColor] (barely
 * visible — like unlit LEDs on a matrix panel).
 */
@Composable
fun NdotDigitText(
    text: String,          // e.g. "21:03" or "21:03:44"
    color: Color,
    dimColor: Color = color.copy(alpha = 0.08f),
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val chars     = text.toList()
        if (chars.isEmpty()) return@Canvas

        val charCount = chars.size
        val charWidth = size.width / charCount
        val dotSize   = (charWidth / 6f).coerceAtMost(size.height / 8f)

        chars.forEachIndexed { index, ch ->
            val left = index * charWidth + (charWidth - dotSize * 5 - dotSize * 0.6f * 4) / 2f
            when {
                ch.isDigit() -> drawNdotDigit(
                    digit    = ch.digitToInt(),
                    left     = left,
                    top      = (size.height - dotSize * 7 - dotSize * 0.5f * 6) / 2f,
                    dotSize  = dotSize,
                    gap      = dotSize * 0.6f,
                    color    = color,
                    dimColor = dimColor
                )
                ch == ':' -> drawNdotColon(
                    left     = left + charWidth / 2f - dotSize / 2f,
                    top      = (size.height - dotSize * 7 - dotSize * 0.5f * 6) / 2f,
                    dotSize  = dotSize,
                    gap      = dotSize * 0.6f,
                    color    = color
                )
            }
        }
    }
}

/** 5-column × 7-row dot bitmaps for digits 0–9. Row 0 = top. */
private val NDOT_DIGITS: Array<LongArray> = arrayOf(
    // 0
    longArrayOf(
        0b01110,
        0b10001,
        0b10011,
        0b10101,
        0b11001,
        0b10001,
        0b01110,
    ),
    // 1
    longArrayOf(
        0b00100,
        0b01100,
        0b00100,
        0b00100,
        0b00100,
        0b00100,
        0b01110,
    ),
    // 2
    longArrayOf(
        0b01110,
        0b10001,
        0b00001,
        0b00110,
        0b01000,
        0b10000,
        0b11111,
    ),
    // 3
    longArrayOf(
        0b11110,
        0b00001,
        0b00001,
        0b01110,
        0b00001,
        0b00001,
        0b11110,
    ),
    // 4
    longArrayOf(
        0b00010,
        0b00110,
        0b01010,
        0b10010,
        0b11111,
        0b00010,
        0b00010,
    ),
    // 5
    longArrayOf(
        0b11111,
        0b10000,
        0b10000,
        0b11110,
        0b00001,
        0b00001,
        0b11110,
    ),
    // 6
    longArrayOf(
        0b01110,
        0b10000,
        0b10000,
        0b11110,
        0b10001,
        0b10001,
        0b01110,
    ),
    // 7
    longArrayOf(
        0b11111,
        0b00001,
        0b00010,
        0b00100,
        0b01000,
        0b01000,
        0b01000,
    ),
    // 8
    longArrayOf(
        0b01110,
        0b10001,
        0b10001,
        0b01110,
        0b10001,
        0b10001,
        0b01110,
    ),
    // 9
    longArrayOf(
        0b01110,
        0b10001,
        0b10001,
        0b01111,
        0b00001,
        0b00001,
        0b01110,
    ),
)

private fun DrawScope.drawNdotDigit(
    digit: Int,
    left: Float,
    top: Float,
    dotSize: Float,
    gap: Float,
    color: Color,
    dimColor: Color
) {
    val bitmap = NDOT_DIGITS[digit]
    val step   = dotSize + gap
    val radius = dotSize / 2f

    for (row in 0..6) {
        val bits = bitmap[row]
        for (col in 0..4) {
            val active = (bits shr (4 - col)) and 1L == 1L
            drawCircle(
                color  = if (active) color else dimColor,
                radius = radius,
                center = Offset(
                    x = left + col * step + radius,
                    y = top  + row * step + radius
                )
            )
        }
    }
}

private fun DrawScope.drawNdotColon(
    left: Float,
    top: Float,
    dotSize: Float,
    gap: Float,
    color: Color
) {
    val step   = dotSize + gap
    val radius = dotSize / 2f
    // Upper dot at row 2, col 0
    drawCircle(color = color, radius = radius,
        center = Offset(left + radius, top + 2 * step + radius))
    // Lower dot at row 4, col 0
    drawCircle(color = color, radius = radius,
        center = Offset(left + radius, top + 4 * step + radius))
}
