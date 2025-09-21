package com.timor.kidsstory.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.manager.FirstRunManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Splash 화면 ViewModel
 * - 첫 실행 여부 확인
 * - 적절한 화면으로 네비게이션 결정
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firstRunManager: FirstRunManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    
    init {
        checkFirstRunAndNavigate()
    }
    
    /**
     * 첫 실행 여부 확인 후 네비게이션 결정
     */
    private fun checkFirstRunAndNavigate() {
        viewModelScope.launch {
            try {
                // 최소 스플래시 시간 보장
                delay(MINIMUM_SPLASH_DURATION)
                
                val isFirstRun = firstRunManager.isFirstRun()
                
                if (isFirstRun) {
                    // 첫 실행: 언어 선택으로
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigateTo = SplashNavigationTarget.LanguageSelection
                    )
                } else {
                    // 재실행: 저장된 언어/레벨로 책장으로
                    val languageAndLevel = firstRunManager.getSelectedLanguageAndLevel()
                    if (languageAndLevel != null) {
                        val (language, level) = languageAndLevel
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            navigateTo = SplashNavigationTarget.Bookshelf(language, level)
                        )
                    } else {
                        // 설정이 없으면 첫 실행으로 처리
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            navigateTo = SplashNavigationTarget.LanguageSelection
                        )
                    }
                }
                
            } catch (e: Exception) {
                // 오류 발생시 언어 선택으로 fallback
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigateTo = SplashNavigationTarget.LanguageSelection,
                    errorMessage = e.message
                )
            }
        }
    }
    
    companion object {
        private const val MINIMUM_SPLASH_DURATION = 1500L // 1.5초
    }
}

/**
 * Splash 화면 UI 상태
 */
data class SplashUiState(
    val isLoading: Boolean = true,
    val navigateTo: SplashNavigationTarget? = null,
    val errorMessage: String? = null
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
    data class Bookshelf(val language: String, val level: Int) : SplashNavigationTarget()
}
