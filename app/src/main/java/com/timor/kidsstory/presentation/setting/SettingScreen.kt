package com.timor.kidsstory.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.setting.components.EmailDialog
import com.timor.kidsstory.presentation.setting.components.WideScreenCardLayout
import com.timor.kidsstory.presentation.setting.components.NormalScreenCardLayout
import com.timor.kidsstory.presentation.setting.components.SettingHeader
import com.timor.kidsstory.presentation.setting.components.SmallScreenCardLayout
import com.timor.kidsstory.presentation.setting.components.VerySmallScreenCardLayout
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 설정 화면 UI 컴포넌트
 * - 갤럭시 폴드 대응 완료
 * - 앱 설정, 개발자 정보, 라이센스 등 표시
 * - 배경 음악 설정 제어
 */
@Composable
fun SettingScreen(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isWideScreen = screenWidth >= 720 // 갤럭시 폴드 등 넓은 화면
    val isVerySmallScreen = screenWidth < 420 // 매우 작은 화면 (420dp 미만) - Pixel 폰 포함
    val isSmallScreen = screenWidth < 500 // 작은 화면 (500dp 미만)
    val isTablet = screenWidth >= 600 // 태블릿 화면 여부

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.primary50)
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더
            SettingHeader(onAction = onAction)

            // 카드 영역 - 화면 크기에 따른 레이아웃 분기
            when {
                isWideScreen -> {
                    WideScreenCardLayout(
                        state = state,
                        onAction = onAction,
                        scaleFactor = scaleFactor,
                        screenWidth = screenWidth,
                        isTablet = isTablet
                    )
                }
                isVerySmallScreen -> {
                    VerySmallScreenCardLayout(
                        state = state,
                        onAction = onAction,
                        scaleFactor = scaleFactor,
                        isTablet = isTablet
                    )
                }
                isSmallScreen -> {
                    SmallScreenCardLayout(
                        state = state,
                        onAction = onAction,
                        scaleFactor = scaleFactor,
                        isTablet = isTablet
                    )
                }
                else -> {
                    NormalScreenCardLayout(
                        state = state,
                        onAction = onAction,
                        scaleFactor = scaleFactor,
                        isTablet = isTablet
                    )
                }
            }
        }

        // 이메일 다이얼로그
        if (state.showEmailDialog) {
            EmailDialog(
                onDismiss = { onAction(SettingAction.DismissEmailDialog) },
                onWebsiteClick = { onAction(SettingAction.WebsiteLinkClick) }
            )
        }
    }
}

// Preview 함수들
@Preview(showBackground = true, widthDp = 393, heightDp = 160, device = "id:pixel_5")
@Composable
fun SettingScreenPixel5Preview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 0.7f, soundEffectVolume = 0.5f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 884, heightDp = 344)
@Composable
fun SettingScreenGalaxyFoldPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 1.0f, soundEffectVolume = 0.9f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
fun SettingScreenTabletPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 1.0f, soundEffectVolume = 0.9f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun SettingScreenSmallPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 0.7f, soundEffectVolume = 0.5f),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SettingScreenVerySmallPreview() {
    KidsStoryTheme {
        SettingScreen(
            state = SettingUiState(isMusicOn = true, isSoundEffectOn = true, musicVolume = 0.7f, soundEffectVolume = 0.5f),
            onAction = {}
        )
    }
}