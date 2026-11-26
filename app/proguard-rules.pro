# Keep Media3 & ExoPlayer classes
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Coil image loader
-keep class coil.** { *; }
-dontwarn coil.**

# Keep Compose & Coroutines
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
