package com.project.givuandtake.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.project.givuandtake.R


// 커스텀 FontFamily 정의
val gmarketSans = FontFamily(
    Font(R.font.gmarket_sans_ttf_medium, FontWeight.Normal),
    Font(R.font.gmarket_sans_ttf_bold, FontWeight.Bold),
    Font(R.font.gmarket_sans_ttf_light, FontWeight.Light)
)
// Set of Material typography styles to start with
// Typography 설정 (기본 Text 스타일 정의)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = gmarketSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = gmarketSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
)

internal val CustomTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = gmarketSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = gmarketSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )
)