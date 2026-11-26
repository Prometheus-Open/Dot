package com.amoled.clockmusic.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amoled.clockmusic.ui.components.*
import com.amoled.clockmusic.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClockScreen(
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var use24Hour by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000L)
        }
    }

    // Subtle AMOLED burn-in protection: shifts clock 0..2dp every minute
    val burnInShiftX = ((now.minute % 3) - 1).dp
    val burnInShiftY = (((now.minute / 3) % 3) - 1).dp

    val hhmm   = now.format(DateTimeFormatter.ofPattern(if (use24Hour) "HH:mm" else "hh:mm"))
    val ss     = now.format(DateTimeFormatter.ofPattern("ss"))
    val date   = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))
    val amPm   = if (use24Hour) "24H" else now.format(DateTimeFormatter.ofPattern("a"))
    val second = now.second

    if (isTablet) {
        ClockTabletLayout(hhmm, ss, date, amPm, second, burnInShiftX, burnInShiftY, { use24Hour = !use24Hour }, modifier)
    } else {
        ClockPhoneLayout(hhmm, ss, date, amPm, second, burnInShiftX, burnInShiftY, { use24Hour = !use24Hour }, modifier)
    }
}

// ─── Tablet — split panel ─────────────────────────────────────────────────────
@Composable
private fun ClockTabletLayout(
    hhmm: String,
    ss: String,
    date: String,
    amPm: String,
    second: Int,
    shiftX: androidx.compose.ui.unit.Dp,
    shiftY: androidx.compose.ui.unit.Dp,
    onToggle24h: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Left: Big NDot clock + Glyph ring ───────────────────────────────
        GlassCard(
            modifier      = Modifier.weight(0.52f).fillMaxHeight(),
            cornerRadius  = 32.dp,
            showDotGrid   = true
        ) {
            Box(
                modifier  = Modifier.fillMaxSize().padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glyph ring behind the clock (seconds ring)
                GlyphRing(
                    progress  = second / 59f,
                    color     = MaterialTheme.colorScheme.primary,
                    dimColor  = NothingRim,
                    dotCount  = 60,
                    pulse     = true,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                // NDot clock in the centre (with subtle burn-in shift)
                Column(
                    modifier = Modifier.offset(x = shiftX, y = shiftY),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 24H / AM/PM toggle badge
                    NothingBadge(
                        text     = amPm,
                        active   = false,
                        modifier = Modifier.clickable { onToggle24h() }
                    )
                    Spacer(Modifier.height(16.dp))

                    // HH:MM in NDot dot-matrix style
                    NdotDigitText(
                        text     = hhmm,
                        color    = MaterialTheme.colorScheme.onBackground,
                        dimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(140.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Seconds in smaller NDot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text  = "·",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        NdotDigitText(
                            text     = ss,
                            color    = MaterialTheme.colorScheme.primary,
                            dimColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            modifier = Modifier.width(60.dp).height(44.dp)
                        )
                    }
                }
            }
        }

        // ── Right: Date card + world clock ───────────────────────────────────
        Column(
            modifier            = Modifier.weight(0.48f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Date card — Nothing style (monochrome, lettering, big number)
            GlassCard(
                modifier     = Modifier.fillMaxWidth().weight(1f),
                cornerRadius = 28.dp,
                showDotGrid  = true
            ) {
                Column(
                    modifier            = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    NothingBadge(text = date.uppercase(), active = false)
                    Spacer(Modifier.height(12.dp))

                    // Large day number — NDot style
                    NdotDigitText(
                        text     = "%02d".format(LocalDateTime.now().dayOfMonth),
                        color    = MaterialTheme.colorScheme.onBackground,
                        dimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                        modifier = Modifier.width(120.dp).height(80.dp)
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = LocalDateTime.now().month.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                            + "  " + LocalDateTime.now().year,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))
                    // Glyph strip — day progress (minutes in day / 1440)
                    val dayProgress = (LocalDateTime.now().hour * 60 + LocalDateTime.now().minute) / 1440f
                    GlyphStrip(
                        progress = dayProgress,
                        color    = MaterialTheme.colorScheme.primary,
                        dotCount = 48,
                        modifier = Modifier.fillMaxWidth().height(10.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "DAY PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // World clock row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WorldClockTile("NEW YORK", -5, Modifier.weight(1f))
                WorldClockTile("LONDON",    0, Modifier.weight(1f))
                WorldClockTile("TOKYO",     9, Modifier.weight(1f))
            }
        }
    }
}

// ─── Phone — stacked ──────────────────────────────────────────────────────────
@Composable
private fun ClockPhoneLayout(
    hhmm: String,
    ss: String,
    date: String,
    amPm: String,
    second: Int,
    shiftX: androidx.compose.ui.unit.Dp,
    shiftY: androidx.compose.ui.unit.Dp,
    onToggle24h: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Big clock card with Glyph ring
        GlassCard(
            modifier     = Modifier.fillMaxWidth().weight(1.4f),
            cornerRadius = 32.dp,
            showDotGrid  = true
        ) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                GlyphRing(
                    progress = second / 59f,
                    color    = MaterialTheme.colorScheme.primary,
                    dimColor = NothingRim,
                    dotCount = 60,
                    pulse    = true,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                )
                Column(
                    modifier = Modifier.offset(x = shiftX, y = shiftY),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NothingBadge(text = amPm, active = false, modifier = Modifier.clickable { onToggle24h() })
                    Spacer(Modifier.height(10.dp))
                    NdotDigitText(
                        text     = hhmm,
                        color    = MaterialTheme.colorScheme.onBackground,
                        dimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                        modifier = Modifier.fillMaxWidth(0.8f).height(110.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("·", style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(4.dp))
                        NdotDigitText(
                            text     = ss,
                            color    = MaterialTheme.colorScheme.primary,
                            dimColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            modifier = Modifier.width(44.dp).height(32.dp)
                        )
                    }
                }
            }
        }

        // Date card
        GlassCard(
            modifier     = Modifier.fillMaxWidth().weight(0.6f),
            cornerRadius = 28.dp,
            showDotGrid  = false
        ) {
            Row(
                modifier              = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    NothingBadge(text = date, active = false)
                    Spacer(Modifier.height(6.dp))
                    NdotDigitText(
                        text     = "%02d".format(LocalDateTime.now().dayOfMonth),
                        color    = MaterialTheme.colorScheme.onBackground,
                        dimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                        modifier = Modifier.width(70.dp).height(50.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        LocalDateTime.now().month.name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val dayProg = (LocalDateTime.now().hour * 60 + LocalDateTime.now().minute) / 1440f
                    GlyphStrip(
                        progress = dayProg,
                        color    = MaterialTheme.colorScheme.primary,
                        dotCount = 24,
                        modifier = Modifier.width(100.dp).height(8.dp)
                    )
                }
            }
        }

        // World clock mini row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WorldClockTile("NYC", -5, Modifier.weight(1f))
            WorldClockTile("LDN",  0, Modifier.weight(1f))
            WorldClockTile("TKY",  9, Modifier.weight(1f))
        }
    }
}

// ─── World clock tile ─────────────────────────────────────────────────────────
@Composable
private fun WorldClockTile(city: String, utcOffset: Int, modifier: Modifier) {
    val localOffset = java.time.ZoneOffset.systemDefault()
        .getRules().getOffset(java.time.Instant.now()).totalSeconds / 3600L
    val cityTime = LocalDateTime.now().plusHours(utcOffset.toLong() - localOffset)
    val t        = cityTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    GlassCard(
        modifier     = modifier.height(72.dp),
        cornerRadius = 20.dp,
        showDotGrid  = false
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                city,
                style         = MaterialTheme.typography.labelSmall,
                color         = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                t,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
