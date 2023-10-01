package cn.xihan.age.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

//object AgeAnimeFontFamilies {
//    val posterFontFamily = FontFamily(Font(R.font.poster_bold, FontWeight.Bold))
//    val cuteFontFamily = FontFamily(Font(R.font.cute_regular, FontWeight.Normal))
//    val heiFontFamily = FontFamily(
//        Font(R.font.hei_bold, FontWeight.Bold),
//        Font(R.font.hei_regular, FontWeight.Normal)
//    )
//}


object AgeAnimeTypography {
    val phone = Typography(
        titleLarge = TextStyle(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
    )
    val tablet = Typography(
        titleLarge = TextStyle(
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Medium,

            ),
        titleMedium = TextStyle(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
        ),
        titleSmall = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Medium,
        ),
        bodyLarge = TextStyle(
            fontSize = 20.sp,
            lineHeight = 28.sp,
        ),
        bodyMedium = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
        ),
        bodySmall = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        labelSmall = TextStyle(
            fontSize = 15.sp,
            lineHeight = 24.sp,
        )
    )
}