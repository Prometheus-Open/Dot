package com.amoled.clockmusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.layout.WindowMetricsCalculator
import com.amoled.clockmusic.media.MusicViewModel
import com.amoled.clockmusic.ui.screens.MainScaffold
import com.amoled.clockmusic.ui.theme.AppTheme
import com.amoled.clockmusic.ui.theme.ClockMusicTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(AppTheme.FUSION) }
            ClockMusicTheme(appTheme = currentTheme) {
                AppRoot(
                    currentTheme = currentTheme,
                    onCycleTheme = {
                        currentTheme = when (currentTheme) {
                            AppTheme.FUSION -> AppTheme.NOTHING
                            AppTheme.NOTHING -> AppTheme.BLOCKIT
                            AppTheme.BLOCKIT -> AppTheme.FUSION
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AppRoot(
    currentTheme: AppTheme,
    onCycleTheme: () -> Unit
) {
    val context    = LocalContext.current
    val activity   = context as ComponentActivity
    val musicVm: MusicViewModel = viewModel()

    // ── Detect tablet vs phone via window width ───────────────────────────────
    val isTablet = remember {
        val metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(activity)
        val widthDp = metrics.bounds.width() /
                (context.resources.displayMetrics.densityDpi / 160f)
        widthDp >= 600f
    }

    // ── Permission request ────────────────────────────────────────────────────
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            musicVm.grantPermission()
            musicVm.init(context)
        }
    }
    LaunchedEffect(Unit) {
        if (hasPermission) {
            musicVm.grantPermission()
            musicVm.init(context)
        } else {
            permLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        MainScaffold(
            isTablet       = isTablet,
            musicViewModel = musicVm,
            currentTheme   = currentTheme,
            onCycleTheme   = onCycleTheme,
            modifier       = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
    }
}
