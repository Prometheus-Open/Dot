package com.amoled.clockmusic.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.amoled.clockmusic.media.MusicViewModel
import com.amoled.clockmusic.ui.components.NothingBadge
import com.amoled.clockmusic.ui.theme.*

enum class AppScreen { CLOCK, MUSIC }

@Composable
fun MainScaffold(
    isTablet: Boolean,
    musicViewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.CLOCK) }

    if (isTablet) {
        TabletScaffold(currentScreen, { currentScreen = it }, musicViewModel, modifier)
    } else {
        PhoneScaffold(currentScreen, { currentScreen = it }, musicViewModel, modifier)
    }
}

// ─── Tablet — floating Nothing-style pill sidebar ────────────────────────────
@Composable
private fun TabletScaffold(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    musicViewModel: MusicViewModel,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState   = currentScreen,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label         = "screen",
            modifier      = Modifier.fillMaxSize()
        ) { screen ->
            when (screen) {
                AppScreen.CLOCK -> ClockScreen(isTablet = true)
                AppScreen.MUSIC -> MusicScreen(musicViewModel, isTablet = true)
            }
        }

        // Nothing-style floating pill sidebar — right edge
        NothingNavPill(
            currentScreen = currentScreen,
            onNavigate    = onNavigate,
            modifier      = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        )
    }
}

// ─── Phone — Nothing-style bottom bar ────────────────────────────────────────
@Composable
private fun PhoneScaffold(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    musicViewModel: MusicViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState   = currentScreen,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label         = "screen",
                modifier      = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    AppScreen.CLOCK -> ClockScreen(isTablet = false)
                    AppScreen.MUSIC -> MusicScreen(musicViewModel, isTablet = false)
                }
            }
        }
        NothingBottomBar(currentScreen, onNavigate)
    }
}

// ─── Nothing floating pill sidebar (tablet) ───────────────────────────────────
@Composable
private fun NothingNavPill(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier
) {
    // Pill shape with circuit-trace border — Nothing aesthetic
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(NothingCharcoal.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = NothingRim,
                shape = shape
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NothingNavItem(
            icon     = Icons.Rounded.Schedule,
            label    = "CLOCK",
            selected = currentScreen == AppScreen.CLOCK,
            onClick  = { onNavigate(AppScreen.CLOCK) }
        )
        // Divider dot — Nothing-esque
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(NothingRim)
        )
        NothingNavItem(
            icon     = Icons.Rounded.LibraryMusic,
            label    = "MUSIC",
            selected = currentScreen == AppScreen.MUSIC,
            onClick  = { onNavigate(AppScreen.MUSIC) }
        )
    }
}

@Composable
private fun NothingNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg   = if (selected) NothingRed else Color.Transparent
    val tint = if (selected) NothingWhite else NothingGray2

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .then(
                if (!selected) Modifier.border(1.dp, NothingRim, RoundedCornerShape(14.dp))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, label, tint = tint, modifier = Modifier.size(22.dp))
    }
}

// ─── Nothing-style bottom bar (phone) ────────────────────────────────────────
@Composable
private fun NothingBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NothingCharcoal)
            .border(
                width   = 0.5.dp,
                color   = NothingRim,
                shape   = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            NothingBottomItem(
                icon     = Icons.Rounded.Schedule,
                label    = "CLOCK",
                selected = currentScreen == AppScreen.CLOCK,
                onClick  = { onNavigate(AppScreen.CLOCK) }
            )
            NothingBottomItem(
                icon     = Icons.Rounded.LibraryMusic,
                label    = "MUSIC",
                selected = currentScreen == AppScreen.MUSIC,
                onClick  = { onNavigate(AppScreen.MUSIC) }
            )
        }
    }
}

@Composable
private fun NothingBottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier            = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon, label,
            tint     = if (selected) NothingRed else NothingGray2,
            modifier = Modifier.size(22.dp)
        )
        // Active indicator — Nothing-style red dot
        Box(
            modifier = Modifier
                .size(if (selected) 5.dp else 0.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(NothingRed)
        )
    }
}
