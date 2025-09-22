package com.timor.kidsstory.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.manager.FirstRunManager
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.service.ContentUpdateService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Splash 화면 ViewModel
 * - 하이브리드 콘텐츠 시스템 초기화
 * - 첫 실행 여부 확인
 * - 콘텐츠 업데이트 체크
 * - 적절한 화면으로 네비게이션 결정
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firstRunManager: FirstRunManager,
    private val hybridContentManager: HybridContentManager,
    private val contentUpdateService: ContentUpdateService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        android.util.Log.d("SplashViewModel", "🚀 SplashViewModel initialized")
        initializeAppContent()
    }

    /**
    * 앱 콘텐츠 전체 초기화 프로세스
    */
    private fun initializeAppContent() {
        android.util.Log.d("SplashViewModel", "🔄 initializeAppContent started")
        viewModelScope.launch {
            try {
                // 1. 하이브리드 콘텐츠 시스템 초기화 (실패해도 계속 진행)
                android.util.Log.d("SplashViewModel", "🔧 Starting hybrid content initialization...")
                _uiState.value = _uiState.value.copy(
                    loadingMessage = "콘텐츠 초기화 중..."
                )
                
                try {
                    val hybridInitResult = hybridContentManager.initializeHybridContent()
                    if (hybridInitResult.isSuccess) {
                        android.util.Log.d("SplashViewModel", "✅ Hybrid content initialized successfully")
                    } else {
                        android.util.Log.w("SplashViewModel", "⚠️ Hybrid content init failed, continuing anyway: ${hybridInitResult.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    android.util.Log.w("SplashViewModel", "⚠️ Hybrid content init exception, continuing anyway", e)
                }
                
                // 2. 최소 스플래시 시간 보장
                android.util.Log.d("SplashViewModel", "⏰ Waiting for minimum splash duration...")
                delay(MINIMUM_SPLASH_DURATION)
                
                // 3. 네비게이션 결정
                android.util.Log.d("SplashViewModel", "🎯 About to call determineNavigationTarget()")
                determineNavigationTarget()
                
            } catch (e: Exception) {
                android.util.Log.e("SplashViewModel", "❌ Exception in initializeAppContent", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigateTo = SplashNavigationTarget.LanguageSelection,
                    errorMessage = e.message,
                    loadingMessage = null
                )
            }
        }
    }

    /**
     * 첫 실행 여부 확인 후 네비게이션 결정
     */
    private suspend fun determineNavigationTarget() {
        try {
            val isFirstRun = firstRunManager.isFirstRun()
            android.util.Log.d("SplashViewModel", "🔍 determineNavigationTarget - isFirstRun: $isFirstRun")

            if (isFirstRun) {
                // 첫 실행: 언어 선택으로
                android.util.Log.d("SplashViewModel", "🎯 First run detected -> LanguageSelection")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigateTo = SplashNavigationTarget.LanguageSelection,
                    loadingMessage = null
                )
            } else {
                // 재실행: 저장된 언어/레벨로 책장으로
                val languageAndLevel = firstRunManager.getSelectedLanguageAndLevel()
                android.util.Log.d("SplashViewModel", "🔍 getSelectedLanguageAndLevel result: $languageAndLevel")

                if (languageAndLevel != null) {
                    val (language, level) = languageAndLevel
                    android.util.Log.d("SplashViewModel", "🎯 Returning user detected -> Bookshelf (language: $language, level: $level)")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigateTo = SplashNavigationTarget.Bookshelf(language, level),
                        loadingMessage = null
                    )
                } else {
                    // 설정이 없으면 첫 실행으로 처리
                    android.util.Log.d("SplashViewModel", "⚠️ No settings found -> LanguageSelection fallback")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigateTo = SplashNavigationTarget.LanguageSelection,
                        loadingMessage = null
                    )
                }
            }

        } catch (e: Exception) {
            // 오류 발생시 언어 선택으로 fallback
            android.util.Log.e("SplashViewModel", "❌ Error in determineNavigationTarget", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                navigateTo = SplashNavigationTarget.LanguageSelection,
                errorMessage = e.message,
                loadingMessage = null
            )
        }
    }

    companion object {
        private const val MINIMUM_SPLASH_DURATION = 2000L // 2초 (하이브리드 초기화 시간 고려)
    }
}

/**
 * Splash 화면 UI 상태
 */
data class SplashUiState(
    val isLoading: Boolean = true,
    val navigateTo: SplashNavigationTarget? = null,
    val errorMessage: String? = null,
    val loadingMessage: String? = null // 로딩 상태 메시지
)

/**
 * Splash에서 이동할 대상 화면
 */
sealed class SplashNavigationTarget {
    /**
     * 언어 선택 화면으로 (첫 실행)
     */
    data object LanguageSelection : SplashNavigationTarget()

    /**
     * 책장 화면으로 (재실행)
     */
    data class Bookshelf(val language: String, val level: Int, val wasSkipped: Boolean = false, val showLevelResultPopup: Boolean = false) : SplashNavigationTarget()
}
