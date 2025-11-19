package com.timor.kidsstory.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * 라이트 모드용 색상 스키마 정의
 *
 * 라이트 모드에서 사용될 주요 색상들을 정의합니다.
 * 🔧 연보라색 ripple을 방지하기 위해 회색 계열로 설정
 */
private val LightColorScheme = lightColorScheme(
    primary = AppColors.unknown500, // 노란색 유지
    onPrimary = AppColors.neutralBlack,
    primaryContainer = AppColors.neutral200, // 🔧 연한 회색으로 변경
    onPrimaryContainer = AppColors.neutral800,
    
    secondary = AppColors.secondary600,
    onSecondary = AppColors.neutralWhite,
    secondaryContainer = AppColors.neutral100, // 🔧 연한 회색으로 변경
    onSecondaryContainer = AppColors.neutral700,
    
    tertiary = AppColors.neutral300, // 🔧 회색으로 변경
    onTertiary = AppColors.neutralWhite,
    tertiaryContainer = AppColors.neutral100, // 🔧 연한 회색으로 변경
    onTertiaryContainer = AppColors.neutral800,
    
    background = AppColors.neutralWhite,
    onBackground = AppColors.neutralBlack,
    
    surface = AppColors.neutralWhite,
    onSurface = AppColors.neutralBlack,
    surfaceVariant = AppColors.neutral100, // 🔧 연한 회색으로 변경
    onSurfaceVariant = AppColors.neutral700,
    
    outline = AppColors.neutral300, // 🔧 회색으로 변경
    outlineVariant = AppColors.neutral200, // 🔧 연한 회색으로 변경
    
    error = AppColors.red600,
    onError = AppColors.neutralWhite,
    errorContainer = AppColors.neutral100, // 🔧 연한 회색으로 변경 (에러도 회색톤)
    onErrorContainer = AppColors.red700,
    
    // 🔧 Ripple 관련 색상들을 회색으로 설정
    surfaceTint = AppColors.neutral200,
    inverseSurface = AppColors.neutral800,
    inverseOnSurface = AppColors.neutral100,
    inversePrimary = AppColors.neutral400,
    
    // 🔧 추가적인 색상들도 회색 계열로 설정
    scrim = AppColors.neutralBlack.copy(alpha = 0.32f)
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
