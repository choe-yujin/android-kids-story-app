package com.timor.kidsstory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material Design 기반 타이포그래피 정의
 * 
 * 앱 전체에서 사용할 기본 텍스트 스타일을 정의합니다.
 * 이 타이포그래피는 Material 3 디자인 시스템의 기본값을 사용하며,
 * 더 구체적인 스타일은 AppTextStyles에서 정의합니다.
 */
val Typography = Typography(
    /**
     * 본문 텍스트에 사용되는 기본 스타일
     * 일반적인 콘텐츠 텍스트에 적합합니다.
     */
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