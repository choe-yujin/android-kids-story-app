package com.timor.kidsstory.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*

/**
 * Splash 화면 Root
 */
@Composable
fun SplashScreenRoot(
    onNavigateToLanguageSelection: () -> Unit,
    onNavigateToBookshelf: (language: String, level: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 네비게이션 처리
    LaunchedEffect(uiState.navigateTo) {
        when (val target = uiState.navigateTo) {
            is SplashNavigationTarget.LanguageSelection -> {
                onNavigateToLanguageSelection()
            }
            is SplashNavigationTarget.Bookshelf -> {
                onNavigateToBookshelf(target.language, target.level)
            }
            null -> {
                // 아직 결정되지 않음
            }
        }
    }
    
    SplashScreen(
        isLoading = uiState.isLoading,
        modifier = modifier
    )
}

/**
 * Splash 화면 UI
 */
@Composable
fun SplashScreen(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    // Lottie 애니메이션 구성
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("splash.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1, // 한 번만 재생
        speed = 1.0f
    )

    // 완전한 전체화면 박스 (상태바, 네비바 영역까지 포함)
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets(0)) // 모든 시스템 인셋 무시
            .background(Color(0xFFFDDF59)), // 노란색 배경 (FDDF59)
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize() // 화면 전체를 채우도록 수정
        )
    }
}
