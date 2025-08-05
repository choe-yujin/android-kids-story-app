package com.timor.kidsstory.presentation.splash

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinish: () -> Unit
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

    // 애니메이션이 끝나면 다음 화면으로 이동
    LaunchedEffect(progress) {
        if (progress == 1.0f) {
            delay(500) // 0.5초 추가 대기
            onFinish()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
    }
}
