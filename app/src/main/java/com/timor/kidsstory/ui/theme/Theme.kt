package com.timor.kidsstory.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * 다크 모드용 색상 스키마 정의
 * 
 * 다크 모드에서 사용될 주요 색상들을 정의합니다.
 * 일반적으로 라이트 모드보다 더 밝은 주요 색상을 사용하여 가독성을 높입니다.
 */
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.primary100,     // 다크 모드에서는 좀 더 밝은 노란색 사용
    secondary = AppColors.secondary200, // 다크 모드에서 더 밝은 보조 색상
    tertiary = AppColors.neutral50      // 다크 모드에서 밝은 중성색
)

/**
 * 라이트 모드용 색상 스키마 정의
 * 
 * 라이트 모드에서 사용될 주요 색상들을 정의합니다.
 * 밝은 배경에 어울리는 약간 어두운 색상들로 구성됩니다.
 */
private val LightColorScheme = lightColorScheme(
    primary = AppColors.primary50,     // 매우 밝은 노란색 배경으로 사용
    secondary = AppColors.secondary600, // 더 진한 보조 색상
    tertiary = AppColors.neutral100     // 밝은 회색 배경

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * 앱의 메인 테마 정의 컴포저블 함수
 * 
 * 앱 전체의 디자인 시스템을 설정하는 테마를 제공합니다.
 * Material 3 디자인 가이드라인을 따르며, 다크 모드와 다이나믹 색상을 지원합니다.
 *
 * @param darkTheme 다크 테마 사용 여부 (기본값: 시스템 설정 따름)
 * @param dynamicColor 다이나믹 색상 사용 여부 (안드로이드 12+ 지원, 기본값: true)
 * @param content 테마가 적용될 컴포저블 콘텐츠
 */
@Composable
fun KidsStoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 적용할 색상 스키마 결정
    val colorScheme = when {
        // 안드로이드 12 이상에서 다이나믹 컬러 지원 시 적용
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // 다이나믹 컬러를 지원하지 않거나 비활성화된 경우 기본 테마 적용
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Material 테마 적용
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}