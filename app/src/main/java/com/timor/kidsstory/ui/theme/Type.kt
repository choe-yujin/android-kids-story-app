package com.timor.kidsstory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
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
import com.timor.kidsstory.R

val pretendard = FontFamily(
    Font(R.font.pretendard_variable, FontWeight.Normal),
    Font(R.font.pretendard_variable, FontWeight.Bold),
    Font(R.font.pretendard_variable, FontWeight.SemiBold),
    Font(R.font.pretendard_variable, FontWeight.Medium),
)

val cookierun = FontFamily(
    Font(R.font.cookierun_regular, FontWeight.Normal),
    Font(R.font.cookierun_bold, FontWeight.Bold),
    Font(R.font.cookierun_black, FontWeight.Black)
)

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
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)