package com.amoled.clockmusic.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.amoled.clockmusic.media.MusicUiState
import com.amoled.clockmusic.media.MusicViewModel
import com.amoled.clockmusic.media.Song
import com.amoled.clockmusic.ui.components.*
import com.amoled.clockmusic.ui.theme.*
import androidx.media3.common.Player
import java.util.concurrent.TimeUnit

@Composable
fun MusicScreen(
    viewModel: MusicViewModel,
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (isTablet) {
        MusicTabletLayout(state, viewModel, context, modifier)
    } else {
        MusicPhoneLayout(state, viewModel, context, modifier)
    }
}

// ─── TABLET ───────────────────────────────────────────────────────────────────
@Composable
private fun MusicTabletLayout(
    state: MusicUiState,
    viewModel: MusicViewModel,
    context: Context,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Left: Album art + controls ───────────────────────────────────────
        GlassCard(
            modifier     = Modifier.weight(0.45f).fillMaxHeight(),
            cornerRadius = 32.dp,
            showDotGrid  = true
        ) {
            Column(
                modifier            = Modifier.fillMaxSize().padding(28.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Album art with Glyph ring wrapper
                GlyphAlbumArt(
                    artUri    = state.currentSong?.albumArtUri,
                    isPlaying = state.isPlaying,
                    progress  = state.progress,
                    modifier  = Modifier.fillMaxWidth().aspectRatio(1f).weight(1f)
                )
                Spacer(Modifier.height(20.dp))
                SongInfo(state.currentSong, Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                MusicProgressBar(
                    progress = state.progress,
                    posMs    = state.positionMs,
                    durMs    = state.durationMs,
                    onSeek   = { viewModel.seekTo(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                MusicControls(
                    isPlaying  = state.isPlaying,
                    shuffleOn  = state.shuffleOn,
                    repeatMode = state.repeatMode,
                    onPlayPause= { viewModel.togglePlayPause() },
                    onNext     = { viewModel.skipNext() },
                    onPrev     = { viewModel.skipPrev() },
                    onShuffle  = { viewModel.toggleShuffle() },
                    onRepeat   = { viewModel.cycleRepeat() },
                )
            }
        }

        // ── Right: Song list ─────────────────────────────────────────────────
        GlassCard(
            modifier     = Modifier.weight(0.55f).fillMaxHeight(),
            cornerRadius = 32.dp,
            showDotGrid  = false
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(vertical = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "LIBRARY",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    NothingBadge(text = "${state.songs.size} TRACKS", active = false)
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(
                    color     = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                    modifier  = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(8.dp))
                SongList(
                    songs       = state.songs,
                    currentSong = state.currentSong,
                    isPlaying   = state.isPlaying,
                    onSongClick = { viewModel.play(it, context) },
                    modifier    = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ─── PHONE ────────────────────────────────────────────────────────────────────
@Composable
private fun MusicPhoneLayout(
    state: MusicUiState,
    viewModel: MusicViewModel,
    context: Context,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard(
            modifier     = Modifier.fillMaxWidth().weight(1f),
            cornerRadius = 32.dp,
            showDotGrid  = true
        ) {
            Column(
                modifier            = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlyphAlbumArt(
                    artUri    = state.currentSong?.albumArtUri,
                    isPlaying = state.isPlaying,
                    progress  = state.progress,
                    modifier  = Modifier.fillMaxWidth(0.72f).aspectRatio(1f)
                )
                SongInfo(state.currentSong, Modifier.fillMaxWidth())
                MusicProgressBar(
                    progress = state.progress,
                    posMs    = state.positionMs,
                    durMs    = state.durationMs,
                    onSeek   = { viewModel.seekTo(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                MusicControls(
                    isPlaying  = state.isPlaying,
                    shuffleOn  = state.shuffleOn,
                    repeatMode = state.repeatMode,
                    onPlayPause= { viewModel.togglePlayPause() },
                    onNext     = { viewModel.skipNext() },
                    onPrev     = { viewModel.skipPrev() },
                    onShuffle  = { viewModel.toggleShuffle() },
                    onRepeat   = { viewModel.cycleRepeat() },
                )
            }
        }
        GlassCard(
            modifier     = Modifier.fillMaxWidth().weight(0.6f),
            cornerRadius = 28.dp,
            showDotGrid  = false
        ) {
            SongList(
                songs       = state.songs,
                currentSong = state.currentSong,
                isPlaying   = state.isPlaying,
                onSongClick = { viewModel.play(it, context) },
                modifier    = Modifier.fillMaxSize().padding(vertical = 8.dp)
            )
        }
    }
}

// ─── Glyph-ringed album art disc ─────────────────────────────────────────────
@Composable
fun GlyphAlbumArt(
    artUri: Any?,
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val rotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "rot"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Outer glyph ring (progress = song progress)
        GlyphRing(
            progress = progress,
            color    = NothingRed,
            dimColor = NothingRim,
            dotCount = 60,
            pulse    = isPlaying,
            modifier = Modifier.fillMaxSize()
        )

        // Vinyl disc — slightly smaller to show ring
        Box(
            modifier = Modifier
                .fillMaxSize(0.76f)
                .clip(CircleShape)
                .rotate(if (isPlaying) rotation else 0f)
        ) {
            if (artUri != null) {
                AsyncImage(
                    model              = artUri,
                    contentDescription = "Album Art",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NothingSlate, NothingCharcoal, NothingBlack)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Dot matrix music note
                    Icon(
                        Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint     = NothingRed,
                        modifier = Modifier.fillMaxSize(0.38f)
                    )
                }
            }
            // Centre hole
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .align(Alignment.Center)
            )
            // Vinyl grooves overlay (subtle radial lines)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.06f),
                                Color.Transparent,
                            )
                        )
                    )
            )
        }
    }
}

// ─── Shared Composables ───────────────────────────────────────────────────────

@Composable
private fun SongInfo(song: Song?, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text     = song?.title ?: "— NO TRACK LOADED —",
            style    = MaterialTheme.typography.titleLarge,
            color    = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        if (song != null) {
            NothingBadge(text = "${song.artist}  ·  ${song.album}", active = false)
        } else {
            NothingBadge(text = "OPEN MUSIC LIBRARY", active = true)
        }
    }
}

@Composable
private fun MusicProgressBar(
    progress: Float,
    posMs: Long,
    durMs: Long,
    onSeek: (Float) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        // Nothing-style glyph dot strip + aligned invisible drag slider
        Box(
            modifier = Modifier.fillMaxWidth().height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            GlyphStrip(
                progress = progress,
                color    = NothingRed,
                dimColor = NothingRim,
                dotCount = 60,
                modifier = Modifier.fillMaxWidth().height(12.dp)
            )
            Slider(
                value         = progress,
                onValueChange = onSeek,
                colors        = SliderDefaults.colors(
                    thumbColor         = NothingRed,
                    activeTrackColor   = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatMs(posMs), style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatMs(durMs), style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MusicControls(
    isPlaying: Boolean,
    shuffleOn: Boolean,
    repeatMode: Int,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Shuffle — Nothing badge style
        IconButton(onClick = onShuffle) {
            Icon(
                Icons.Rounded.Shuffle,
                "Shuffle",
                tint     = if (shuffleOn) NothingRed else NothingGray2,
                modifier = Modifier.size(22.dp)
            )
        }
        // Skip prev
        IconButton(onClick = onPrev, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Rounded.SkipPrevious, "Previous",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp))
        }
        // Play/Pause — Nothing-style: square-cornered circle (squircle feel)
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(22.dp))   // squircle — not full circle
                .background(NothingRed)
                .clickable { onPlayPause() }
                .border(1.dp, NothingRed.copy(alpha = 0.4f), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint               = NothingWhite,
                modifier           = Modifier.size(34.dp)
            )
        }
        // Skip next
        IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Rounded.SkipNext, "Next",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp))
        }
        // Repeat
        IconButton(onClick = onRepeat) {
            Icon(
                imageVector = when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> Icons.Rounded.RepeatOne
                    else                   -> Icons.Rounded.Repeat
                },
                contentDescription = "Repeat",
                tint = when (repeatMode) {
                    Player.REPEAT_MODE_OFF -> NothingGray2
                    else                   -> NothingRed
                },
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SongList(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier            = modifier,
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        itemsIndexed(songs, key = { _, s -> s.id }) { _, song ->
            val isActive = song == currentSong
            SongRow(song = song, isActive = isActive, isPlaying = isActive && isPlaying,
                onClick = { onSongClick(song) })
        }
    }
}

@Composable
private fun SongRow(song: Song, isActive: Boolean, isPlaying: Boolean, onClick: () -> Unit) {
    GlassTile(
        modifier     = Modifier.fillMaxWidth().clickable { onClick() },
        cornerRadius = 14.dp,
        selected     = isActive
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NothingSlate),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = song.albumArtUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isPlaying) {
                    Box(
                        Modifier.fillMaxSize().background(Color.Black.copy(0.55f)),
                        Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Equalizer, null, tint = NothingRed,
                            modifier = Modifier.size(18.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = song.title,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = if (isActive) MaterialTheme.colorScheme.inverseOnSurface
                               else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text     = song.artist,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = if (isActive) MaterialTheme.colorScheme.inverseOnSurface.copy(0.6f)
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                formatMs(song.duration),
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) MaterialTheme.colorScheme.inverseOnSurface.copy(0.5f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatMs(ms: Long): String {
    if (ms <= 0) return "0:00"
    val min = TimeUnit.MILLISECONDS.toMinutes(ms)
    val sec = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return "%d:%02d".format(min, sec)
}
