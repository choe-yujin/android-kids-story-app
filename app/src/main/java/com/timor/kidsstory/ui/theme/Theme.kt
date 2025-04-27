package com.timor.kidsstory.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * 라이트 모드용 색상 스키마 정의
 *
 * 라이트 모드에서 사용될 주요 색상들을 정의합니다.
 */
private val LightColorScheme = lightColorScheme(
    primary = AppColors.unknown500,
    secondary = AppColors.secondary600,
    tertiary = AppColors.neutral100
)

/**
 * 앱의 메인 테마 정의 컴포저블 함수
 *
 * 앱 전체의 디자인 시스템을 설정하는 테마를 제공합니다.
 *
 * @param content 테마가 적용될 컴포저블 콘텐츠
 */
@Composable
fun KidsStoryTheme(
    content: @Composable () -> Unit
) {
    // 강제로 라이트 테마만 적용
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}