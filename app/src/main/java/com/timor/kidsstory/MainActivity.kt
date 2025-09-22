package com.timor.kidsstory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.timor.kidsstory.di.UseCaseEntryPoint
import com.timor.kidsstory.domain.model.AppVersionInfo
import com.timor.kidsstory.presentation.bookshelf.components.AppUpdateDialog
import androidx.compose.runtime.*
import android.util.Log
import kotlinx.coroutines.delay
import com.timor.kidsstory.domain.util.LocaleHelper.updateLanguage
import com.timor.kidsstory.presentation.navigation.NavGraph
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * 앱의 진입점 - 앱의 기본 설정을 담당
 * - 전체화면 및 Edge-to-edge 화면 설정
 * - 상태바 숨김 처리
 * - 앱의 메인 네비게이션 그래프 설정
 * - 앱 업데이트 체크
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge와 전체 화면 설정 - 가능한 많은 화면 공간 확보
        enableEdgeToEdge()

        // 시스템 UI (상태바, 네비게이션바) 완전히 숨기기
        hideSystemUI()

        // 언어 설정이 완료된 후에 UI 구성
        applyLanguageSetting {
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

    override fun onResume() {
        super.onResume()
        // 다시 포그라운드로 돌아올 때 시스템 UI 숨김
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // 포커스를 다시 얻었을 때 시스템 UI 숨김
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * 앱 업데이트 체크
     */
    private fun checkForAppUpdates(onUpdateFound: (AppVersionInfo?) -> Unit) {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext, 
            UseCaseEntryPoint::class.java
        )
        val checkAppVersionUseCase = entryPoint.getCheckAppVersionUseCase()

        lifecycleScope.launch {
            try {
                // 앱 시작 후 잠시 대기 (UI 로딩 완료 후 체크)
                delay(2000)
                
                checkAppVersionUseCase().fold(
                    onSuccess = { versionInfo ->
                        if (versionInfo != null) {
                            Log.d("MainActivity", "Update available: ${versionInfo.latestVersionName}")
                            onUpdateFound(versionInfo)
                        } else {
                            Log.d("MainActivity", "App is up to date or update check postponed")
                            onUpdateFound(null)
                        }
                    },
                    onFailure = { error ->
                        Log.w("MainActivity", "Update check failed (ignored)", error)
                        onUpdateFound(null) // 네트워크 오류 등은 무시
                    }
                )
            } catch (e: Exception) {
                Log.e("MainActivity", "Update check exception (ignored)", e)
                onUpdateFound(null)
            }
        }
    }

    /**
     * "나중에" 버튼 클릭 시 처리
     */
    private fun handlePostponeUpdate(versionCode: Int) {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext, 
            UseCaseEntryPoint::class.java
        )
        val postponeUpdateUseCase = entryPoint.getPostponeUpdateUseCase()
        
        lifecycleScope.launch {
            try {
                // 3일간 연기 (72시간)
                postponeUpdateUseCase(versionCode, postponeHours = 72L)
                Log.d("MainActivity", "Update postponed for 3 days for version $versionCode")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error postponing update", e)
            }
        }
    }

    /**
     * 언어 설정 적용 (시스템 언어 무시하고 앱 설정 사용)
     */
    private fun applyLanguageSetting(onLanguageApplied: () -> Unit) {
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, UseCaseEntryPoint::class.java)
        val loadLanguageUseCase = entryPoint.getUserPreferenceUseCase()

        lifecycleScope.launch {
            // collect 대신 first() 사용하여 한 번만 실행
            val userPref = loadLanguageUseCase().first()
            Logger.e("사용자 설정 확인: $userPref")
            
            // 첫 실행이 아니고 언어가 설정되어 있을 때만 언어 적용
            if (!userPref.isFirstRun && userPref.languageCode.isNotBlank()) {
                Logger.e("저장된 언어 적용: ${userPref.languageCode}")
                updateLanguage(userPref.languageCode)
            } else {
                // 첫 실행이거나 언어가 설정되지 않은 경우 기본 언어 사용
                Logger.e("기본 언어 사용: en")
                updateLanguage("en")
            }
            
            onLanguageApplied()
        }
    }
}
