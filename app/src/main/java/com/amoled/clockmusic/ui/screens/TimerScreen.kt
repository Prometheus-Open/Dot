package com.amoled.clockmusic.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amoled.clockmusic.ui.components.GlassCard
import com.amoled.clockmusic.ui.components.GlassTile
import com.amoled.clockmusic.ui.components.NdotDigitText
import com.amoled.clockmusic.ui.components.NothingBadge
import com.amoled.clockmusic.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private data class TimerPreset(val label: String, val unit: String, val minutes: Int)

private val PRESETS = listOf(
    TimerPreset("15", "Minutes", 15),
    TimerPreset("45", "Minutes", 45),
    TimerPreset("3", "Hours", 180),
    TimerPreset("12:00", "Custom", 720),
)

@Composable
fun TimerScreen(
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedPresetIdx by remember { mutableStateOf(0) }
    var totalSeconds by remember { mutableStateOf(PRESETS[0].minutes * 60) }
    var remainingSeconds by remember { mutableStateOf(PRESETS[0].minutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var keepScreenOn by remember { mutableStateOf(true) }
    var strictMode by remember { mutableStateOf(false) }

    val context = LocalContext.current
    DisposableEffect(keepScreenOn, isRunning) {
        val window = (context as? Activity)?.window
        if (keepScreenOn && isRunning) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
            if (remainingSeconds == 0) isRunning = false
        }
    }

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeText = "%02d:%02d".format(mins.coerceAtMost(99), secs)

    if (isTablet) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Huge NDot Timer Display
            GlassCard(
                modifier = Modifier.weight(0.48f).fillMaxHeight(),
                cornerRadius = 32.dp,
                showDotGrid = true
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(28.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NothingBadge(
                        text = if (isRunning) "FOCUS ACTIVE" else "READY",
                        active = isRunning
                    )
                    Spacer(Modifier.height(24.dp))
                    NdotDigitText(
                        text = timeText,
                        color = MaterialTheme.colorScheme.onBackground,
                        dimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                        modifier = Modifier.fillMaxWidth(0.85f).height(140.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    // Distraction-reducing toggles
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NothingBadge(
                            text = if (keepScreenOn) "SCREEN ON" else "AUTO SLEEP",
                            active = keepScreenOn,
                            modifier = Modifier.clickable { keepScreenOn = !keepScreenOn }
                        )
                        NothingBadge(
                            text = if (strictMode) "STRICT LOCK" else "NORMAL",
                            active = strictMode,
                            modifier = Modifier.clickable { if (!isRunning) strictMode = !strictMode }
                        )
                    }
                }
            }

            // Right: 2x2 Preset Grid + Vertical Tick Ruler + Slide to Start
            Column(
                modifier = Modifier.weight(0.52f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 2x2 Preset Grid
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PresetTile(PRESETS[0], selectedPresetIdx == 0, Modifier.weight(1f).fillMaxHeight()) {
                                selectedPresetIdx = 0
                                totalSeconds = PRESETS[0].minutes * 60
                                remainingSeconds = totalSeconds
                                isRunning = false
                            }
                            PresetTile(PRESETS[1], selectedPresetIdx == 1, Modifier.weight(1f).fillMaxHeight()) {
                                selectedPresetIdx = 1
                                totalSeconds = PRESETS[1].minutes * 60
                                remainingSeconds = totalSeconds
                                isRunning = false
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PresetTile(PRESETS[2], selectedPresetIdx == 2, Modifier.weight(1f).fillMaxHeight()) {
                                selectedPresetIdx = 2
                                totalSeconds = PRESETS[2].minutes * 60
                                remainingSeconds = totalSeconds
                                isRunning = false
                            }
                            PresetTile(PRESETS[3], selectedPresetIdx == 3, Modifier.weight(1f).fillMaxHeight()) {
                                selectedPresetIdx = 3
                                totalSeconds = PRESETS[3].minutes * 60
                                remainingSeconds = totalSeconds
                                isRunning = false
                            }
                        }
                    }

                    // Vertical Tick Ruler (like Blockit screenshot 2)
                    VerticalTickRuler(
                        minutes = remainingSeconds / 60,
                        onMinutesChange = { newMin ->
                            val clamped = newMin.coerceIn(1, 180)
                            totalSeconds = clamped * 60
                            remainingSeconds = totalSeconds
                            isRunning = false
                        },
                        modifier = Modifier.width(64.dp).fillMaxHeight()
                    )
                }

                // Slide to Start / Stop
                SlideToStartBar(
                    isRunning = isRunning,
                    onToggle = { if (!(isRunning && strictMode)) isRunning = !isRunning },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    } else {
        // Phone Layout
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().weight(0.9f),
                cornerRadius = 28.dp,
                showDotGrid = true
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NothingBadge(text = if (isRunning) "FOCUS ACTIVE" else "READY", active = isRunning)
                    Spacer(Modifier.height(14.dp))
                    NdotDigitText(
                        text = timeText,
                        color = MaterialTheme.colorScheme.onBackground,
                        dimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                        modifier = Modifier.fillMaxWidth(0.85f).height(96.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.weight(0.7f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PRESETS.forEachIndexed { idx, preset ->
                    PresetTile(preset, selectedPresetIdx == idx, Modifier.weight(1f).fillMaxHeight()) {
                        selectedPresetIdx = idx
                        totalSeconds = preset.minutes * 60
                        remainingSeconds = totalSeconds
                        isRunning = false
                    }
                }
            }

            SlideToStartBar(
                isRunning = isRunning,
                onToggle = { isRunning = !isRunning },
                modifier = Modifier.fillMaxWidth().height(60.dp)
            )
        }
    }
}

@Composable
private fun PresetTile(
    preset: TimerPreset,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassTile(
        modifier = modifier.clickable { onClick() },
        cornerRadius = 22.dp,
        selected = selected
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = preset.label,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) DeepBrown else CreamWhite
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = preset.unit,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) DeepBrown.copy(alpha = 0.65f) else CreamDim
            )
        }
    }
}

@Composable
private fun VerticalTickRuler(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tick ruler box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceBrown)
                .border(1.dp, RimBrown, RoundedCornerShape(18.dp))
                .pointerInput(minutes) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -8f) onMinutesChange(minutes + 1)
                        else if (dragAmount > 8f) onMinutesChange(minutes - 1)
                    }
                }
                .padding(vertical = 12.dp, horizontal = 10.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ticks = 28
                val step = size.height / ticks
                val activeIdx = ((1f - (minutes.coerceIn(1, 180) / 180f)) * ticks).toInt().coerceIn(0, ticks - 1)
                for (i in 0 until ticks) {
                    val y = i * step + step / 2f
                    val isActive = i == activeIdx
                    val lineW = if (isActive) size.width * 0.75f else size.width * 0.45f
                    drawRoundRect(
                        color = if (isActive) NothingRed else CreamDim.copy(alpha = 0.4f),
                        topLeft = Offset((size.width - lineW) / 2f, y - 1.5f),
                        size = Size(lineW, if (isActive) 4f else 2f),
                        cornerRadius = CornerRadius(2f)
                    )
                }
            }
        }

        // Up / Down chevron buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceBrown)
                .border(1.dp, RimBrown, RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMinutesChange(minutes + 5) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, "Increase", tint = CreamWhite, modifier = Modifier.size(20.dp))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMinutesChange(minutes - 5) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, "Decrease", tint = CreamWhite, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun SlideToStartBar(
    isRunning: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var trackWidthPx by remember { mutableStateOf(0f) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    val thumbSizeDp = 52.dp
    val thumbSizePx = with(LocalDensity.current) { thumbSizeDp.toPx() }
    val maxDrag = (trackWidthPx - thumbSizePx - 16f).coerceAtLeast(1f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(if (isRunning) NothingRedDim else SurfaceBrown)
            .border(
                1.dp,
                if (isRunning) NothingRed.copy(alpha = 0.5f) else RimBrown,
                RoundedCornerShape(22.dp)
            )
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .padding(6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (isRunning) "SLIDE TO STOP" else "SLIDE TO START",
            style = MaterialTheme.typography.labelMedium,
            color = if (isRunning) NothingRed else CreamMid,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffsetX.roundToInt(), 0) }
                .size(thumbSizeDp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isRunning) NothingRed else CreamWhite)
                .pointerInput(isRunning, maxDrag) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetX > maxDrag * 0.65f) {
                                onToggle()
                            }
                            dragOffsetX = 0f
                        },
                        onDragCancel = { dragOffsetX = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffsetX = (dragOffsetX + dragAmount).coerceIn(0f, maxDrag)
                        }
                    )
                }
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                contentDescription = if (isRunning) "Stop" else "Start",
                tint = if (isRunning) NothingWhite else DeepBrown,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
