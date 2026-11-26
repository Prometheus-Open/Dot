package com.amoled.clockmusic.media

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MusicUiState(
    val songs: List<Song>          = emptyList(),
    val currentSong: Song?         = null,
    val isPlaying: Boolean         = false,
    val progress: Float            = 0f,         // 0..1
    val positionMs: Long           = 0L,
    val durationMs: Long           = 0L,
    val shuffleOn: Boolean         = false,
    val repeatMode: Int            = Player.REPEAT_MODE_OFF,
    val isLoading: Boolean         = true,
    val hasPermission: Boolean     = false,
)

class MusicViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var appContext: Context? = null

    // ── Initialise ────────────────────────────────────────────────────────────
    fun init(context: Context) {
        appContext = context.applicationContext
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicPlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture!!.addListener({
            controller = controllerFuture!!.get()
            controller?.addListener(playerListener)
            startProgressTracker()
        }, MoreExecutors.directExecutor())

        loadSongs(context)
    }

    fun grantPermission() {
        _uiState.value = _uiState.value.copy(hasPermission = true)
    }

    private fun loadSongs(context: Context) {
        viewModelScope.launch {
            val songs = MusicScanner.scanLocalSongs(context)
            _uiState.value = _uiState.value.copy(songs = songs, isLoading = false)
        }
    }

    // ── Controls ──────────────────────────────────────────────────────────────
    fun play(song: Song, context: android.content.Context) {
        val ctrl = controller ?: return
        val items = _uiState.value.songs.map { with(MusicScanner) { it.toMediaItem() } }
        val idx   = _uiState.value.songs.indexOf(song)
        if (idx >= 0) {
            ctrl.setMediaItems(items, idx, 0L)
            ctrl.prepare()
            ctrl.play()
        }
    }

    fun playAll() {
        val ctrl  = controller ?: return
        val items = _uiState.value.songs.map { with(MusicScanner) { it.toMediaItem() } }
        ctrl.setMediaItems(items)
        ctrl.prepare()
        ctrl.play()
    }

    fun togglePlayPause() {
        val ext = activeExternalController()
        if (ext != null) {
            val state = ext.playbackState?.state
            if (state == android.media.session.PlaybackState.STATE_PLAYING) ext.transportControls.pause()
            else ext.transportControls.play()
            return
        }
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun skipNext() {
        val ext = activeExternalController()
        if (ext != null) { ext.transportControls.skipToNext(); return }
        controller?.seekToNextMediaItem()
    }

    fun skipPrev() {
        val ext = activeExternalController()
        if (ext != null) { ext.transportControls.skipToPrevious(); return }
        controller?.seekToPreviousMediaItem()
    }

    private fun activeExternalController(): android.media.session.MediaController? {
        val ctx = appContext ?: return null
        return try {
            val mgr = ctx.getSystemService(Context.MEDIA_SESSION_SERVICE) as android.media.session.MediaSessionManager
            val comp = ComponentName(ctx, DotNotificationListenerService::class.java)
            mgr.getActiveSessions(comp).firstOrNull()
        } catch (_: SecurityException) {
            null
        }
    }

    fun seekTo(fraction: Float) {
        val dur = _uiState.value.durationMs
        if (dur > 0) controller?.seekTo((dur * fraction).toLong())
    }

    fun toggleShuffle() {
        val ctrl = controller ?: return
        val newShuffle = !ctrl.shuffleModeEnabled
        ctrl.shuffleModeEnabled = newShuffle
        _uiState.value = _uiState.value.copy(shuffleOn = newShuffle)
    }

    fun cycleRepeat() {
        val ctrl = controller ?: return
        val next = when (ctrl.repeatMode) {
            Player.REPEAT_MODE_OFF  -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL  -> Player.REPEAT_MODE_ONE
            else                    -> Player.REPEAT_MODE_OFF
        }
        ctrl.repeatMode = next
        _uiState.value = _uiState.value.copy(repeatMode = next)
    }

    // ── Progress & external MediaSession tracker ─────────────────────────────
    private fun startProgressTracker() {
        viewModelScope.launch {
            while (true) {
                delay(500L)
                val ext = activeExternalController()
                if (ext != null) {
                    val meta = ext.metadata
                    val pb = ext.playbackState
                    val title = meta?.getString(android.media.MediaMetadata.METADATA_KEY_TITLE)
                    val artist = meta?.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST) ?: ""
                    val album = meta?.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM) ?: ""
                    val dur = meta?.getLong(android.media.MediaMetadata.METADATA_KEY_DURATION) ?: 0L
                    val pos = pb?.position ?: 0L
                    val playing = pb?.state == android.media.session.PlaybackState.STATE_PLAYING
                    val prog = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
                    if (title != null) {
                        _uiState.value = _uiState.value.copy(
                            currentSong = Song(-1L, title, artist, album, dur, android.net.Uri.EMPTY, null),
                            isPlaying = playing,
                            positionMs = pos,
                            durationMs = dur,
                            progress = prog
                        )
                    }
                    continue
                }
                val ctrl = controller ?: continue
                val pos  = ctrl.currentPosition
                val dur  = ctrl.duration.coerceAtLeast(1L)
                val prog = (pos.toFloat() / dur).coerceIn(0f, 1f)
                _uiState.value = _uiState.value.copy(
                    positionMs = pos,
                    durationMs = ctrl.duration,
                    progress   = prog
                )
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val id  = mediaItem?.mediaId?.toLongOrNull() ?: return
            val song = _uiState.value.songs.find { it.id == id }
            _uiState.value = _uiState.value.copy(currentSong = song)
        }
    }

    override fun onCleared() {
        controller?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()
    }
}
