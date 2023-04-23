package gcp.global.jotdiary.view.theme

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = DarkMode,
    background = DarkModeDarkest,
    surface = DarkModeDarker,
    onSurface = DarkModeLightest,
)

private val LightColorPalette = lightColors(
    primary = Beige,
    background = BeigeDarker,
    surface = BeigeLightest,
    onSurface = BeigeDarkest,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun JotDiaryTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    val window = (view.context as Activity).window

    if (!view.isInEditMode) {
        SideEffect {
            if (darkTheme) {
                window.statusBarColor = colors.onSurface.toArgb()
                window.navigationBarColor = colors.onSurface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            } else {
                window.statusBarColor = colors.onSurface.toArgb()
                window.navigationBarColor = colors.onSurface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            }
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}