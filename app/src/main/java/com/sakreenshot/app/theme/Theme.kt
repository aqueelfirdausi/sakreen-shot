package com.sakreenshot.app.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AccentBronze,
    onPrimary = BeigeBackground,
    secondary = TextSecondary,
    onSecondary = BeigeSurface,
    background = BeigeBackground,
    onBackground = TextPrimary,
    surface = BeigeSurface,
    onSurface = TextPrimary,
    surfaceVariant = BeigeSurfaceElevated,
    onSurfaceVariant = TextPrimary,
    outline = BorderBronze,
    error = ColorDelete,
    onError = BeigeSurface
)

@Composable
fun SakreenShotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Ignoring dark theme for MVP, using one color palette as per design language
    content: @Composable () -> Unit
) {
    // The prompt explicitly states: "no neon, no cyberpunk, calm, luxurious... one palette"
    // So we'll force the light theme palette for now to match the "parchment/beige" look.
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
