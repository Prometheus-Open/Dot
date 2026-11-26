package com.amoled.clockmusic.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amoled.clockmusic.ui.components.GlassCard
import com.amoled.clockmusic.ui.components.GlassTile
import com.amoled.clockmusic.ui.theme.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private enum class StatsRange(val label: String) {
    DAY("Day"), MONTH("Month"), YEAR("Year")
}

@Composable
fun StatsScreen(
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf(StatsRange.MONTH) }
    var dialAngleDeg by remember { mutableStateOf(15f) } // 0..360
    val selectedDay = ((dialAngleDeg / 360f) * 30f).toInt().coerceIn(1, 30)

    if (isTablet) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Bar chart / activity tick graph (like screenshot 3)
            GlassCard(
                modifier = Modifier.weight(0.45f).fillMaxHeight(),
                cornerRadius = 32.dp,
                showDotGrid = true
            ) {
                ActivityBarChart(
                    activeIndex = selectedDay - 1,
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                )
            }

            // Right: Month header + Day/Month/Year selector + Circular Dial
            Column(
                modifier = Modifier.weight(0.55f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "NOVEMBER 2026  ·  ${selectedDay * 2} MIN",
                    style = MaterialTheme.typography.labelMedium,
                    color = CreamMid,
                    letterSpacing = 1.8.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Month navigator bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceBrown)
                        .border(1.dp, RimBrown, RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { dialAngleDeg = (dialAngleDeg - 12f + 360f) % 360f }) {
                        Icon(Icons.Rounded.ChevronLeft, "Previous", tint = CreamWhite)
                    }
                    Text("NOVEMBER 2026", style = MaterialTheme.typography.labelLarge, color = CreamWhite)
                    IconButton(onClick = { dialAngleDeg = (dialAngleDeg + 12f) % 360f }) {
                        Icon(Icons.Rounded.ChevronRight, "Next", tint = CreamWhite)
                    }
                }

                // Day/Month/Year + Circular Dial
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.width(88.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatsRange.entries.forEach { range ->
                            GlassTile(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clickable { selectedRange = range },
                                cornerRadius = 18.dp,
                                selected = selectedRange == range
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = range.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selectedRange == range) DeepBrown else CreamMid
                                    )
                                }
                            }
                        }
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        cornerRadius = 28.dp,
                        showDotGrid = false
                    ) {
                        CircularDial(
                            angleDeg = dialAngleDeg,
                            onAngleChange = { dialAngleDeg = it },
                            modifier = Modifier.fillMaxSize().padding(20.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Phone layout
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().weight(0.8f),
                cornerRadius = 28.dp,
                showDotGrid = true
            ) {
                ActivityBarChart(
                    activeIndex = selectedDay - 1,
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                )
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth().weight(1f),
                cornerRadius = 28.dp,
                showDotGrid = false
            ) {
                CircularDial(
                    angleDeg = dialAngleDeg,
                    onAngleChange = { dialAngleDeg = it },
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CircularDial(
    angleDeg: Float,
    onAngleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures { change, _ ->
                change.consume()
                val cx = size.width / 2f
                val cy = size.height / 2f
                val dx = change.position.x - cx
                val dy = change.position.y - cy
                val rad = atan2(dy, dx)
                val deg = ((Math.toDegrees(rad.toDouble()).toFloat() + 360f) % 360f)
                onAngleChange(deg)
            }
        }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerR = minOf(cx, cy) - 8f
        val innerR = outerR * 0.78f

        // Inner circle border
        drawCircle(
            color = RimBrown,
            radius = innerR,
            center = Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )

        // Radial tick marks around perimeter (36 ticks)
        val ticks = 36
        for (i in 0 until ticks) {
            val a = Math.toRadians((i * (360.0 / ticks)))
            val sx = cx + (innerR + 10f) * cos(a).toFloat()
            val sy = cy + (innerR + 10f) * sin(a).toFloat()
            val ex = cx + outerR * cos(a).toFloat()
            val ey = cy + outerR * sin(a).toFloat()
            drawLine(
                color = CreamDim.copy(alpha = 0.45f),
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = 2f
            )
        }

        // Active glowing dot inside the dial
        val activeRad = Math.toRadians(angleDeg.toDouble())
        val dotR = innerR * 0.65f
        val dotX = cx + dotR * cos(activeRad).toFloat()
        val dotY = cy + dotR * sin(activeRad).toFloat()
        drawCircle(
            color = NothingRed,
            radius = 16f,
            center = Offset(dotX, dotY)
        )
    }
}

@Composable
private fun ActivityBarChart(
    activeIndex: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val bars = 30
        val step = size.width / bars
        val barW = (step * 0.45f).coerceAtLeast(4f)
        val baseY = size.height - 12f

        for (i in 0 until bars) {
            val x = i * step + (step - barW) / 2f
            val isActive = i == (activeIndex % bars)
            val barH = if (isActive) size.height * 0.55f else 8f
            drawRoundRect(
                color = if (isActive) NothingRed else if (i < activeIndex) CreamWhite else CreamDim.copy(alpha = 0.35f),
                topLeft = Offset(x, baseY - barH),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(3f)
            )
        }
    }
}
