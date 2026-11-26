package com.amoled.clockmusic

import com.amoled.clockmusic.media.MusicUiState
import com.amoled.clockmusic.ui.screens.AppScreen
import com.amoled.clockmusic.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class DotUnitTest {
    @Test
    fun appThemes_hasThreeModes() {
        assertEquals(3, AppTheme.entries.size)
    }

    @Test
    fun appScreens_hasFourTabs() {
        assertEquals(4, AppScreen.entries.size)
    }

    @Test
    fun musicUiState_defaultsAreSafe() {
        val state = MusicUiState()
        assertNull(state.currentSong)
        assertFalse(state.isPlaying)
        assertEquals(0f, state.progress, 0.001f)
    }
}
