package gcp.global.jotdiary.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = OceanBlue,
    primaryVariant = OceanBlueDark,
    secondary = Beige,
    background = OceanBlueLight,
    onPrimary = OceanBlueDarkest,
)

private val LightColorPalette = lightColors(
    primary = Beige,
    primaryVariant = BeigeDark,
    secondary = BeigeDarkest,
    background = BeigeDarker,
    onPrimary = Beige,
    surface = BeigeLight,
    onSurface = BeigeDarkest,
    onBackground = BeigeLight,
    onError = Beige

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
fun JotDiaryTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}