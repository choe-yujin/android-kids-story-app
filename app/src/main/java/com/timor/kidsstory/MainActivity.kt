package com.timor.kidsstory

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.timor.kidsstory.presentation.navigation.NavGraph
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 앱의 진입점 - 앱의 기본 설정을 담당
 * - 가로 모드 고정 설정
 * - 전체화면 및 Edge-to-edge 화면 설정
 * - 상태바 숨김 처리
 * - 앱의 메인 네비게이션 그래프 설정
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 가로 모드 고정 - 어린이용 동화책 앱에 최적화된 레이아웃을 위함
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Edge-to-edge와 전체 화면 설정 - 가능한 많은 화면 공간 확보
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Status bar 숨기기
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Compose UI 설정
        setContent {
            KidsStoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph() // 내비게이션 그래프 설정
                }
            }
        }
    }
}