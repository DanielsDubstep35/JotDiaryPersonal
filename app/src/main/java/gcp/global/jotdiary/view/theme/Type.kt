package gcp.global.jotdiary.view.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import gcp.global.jotdiary.R

// Set of Material typography styles to start with

val fontfamily = FontFamily(
    Font(R.font.lexend_deca)
)

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = fontfamily,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 5.sp
    ),
    body1 = TextStyle(
        fontFamily = fontfamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)