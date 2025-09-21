package com.timor.kidsstory.presentation.languageselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.manager.FirstRunManager
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.util.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 언어 선택 화면 ViewModel
 */
@HiltViewModel
class LanguageSelectionViewModel @Inject constructor(
    private val firstRunManager: FirstRunManager,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LanguageSelectionUiState())
    val uiState: StateFlow<LanguageSelectionUiState> = _uiState.asStateFlow()
    
    init {
        loadAvailableLanguages()
    }
    
    /**
     * 액션 처리
     */
    fun onAction(action: LanguageSelectionAction) {
        when (action) {
            is LanguageSelectionAction.OnLanguageSelected -> {
                selectLanguage(action.languageCode)
            }
        }
    }
    
    /**
     * 사용 가능한 언어 목록 로드
     */
    private fun loadAvailableLanguages() {
        val availableLanguages = listOf(
            Language(
                code = "en",
                displayName = "English",
                flagResId = R.drawable.flag_en
            ),
            Language(
                code = "ko", 
                displayName = "한국어",
                flagResId = R.drawable.flag_ko
            ),
            Language(
                code = "tet",
                displayName = "Tetun",
                flagResId = R.drawable.flag_tet
            )
        )
        
        _uiState.value = _uiState.value.copy(
            availableLanguages = availableLanguages,
            isLoading = false
        )
    }
    
    /**
     * 언어 선택 처리
     */
    private fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedLanguage = _uiState.value.availableLanguages.find { it.code == languageCode },
                isLoading = true
            )
            
            try {
                // 네트워크 상태 확인
                val isNetworkAvailable = NetworkUtils.isNetworkAvailable(context)
                
                if (isNetworkAvailable) {
                    // 인터넷 사용 가능: 레벨 테스트 진행
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigationTarget = LanguageSelectionNavigationTarget.LevelTest(languageCode)
                    )
                } else {
                    // 오프라인: 기본 레벨로 책장 진입
                    firstRunManager.skipLevelTest(languageCode)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigationTarget = LanguageSelectionNavigationTarget.Bookshelf(languageCode, 0) // 0 = All levels
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Language selection failed"
                )
            }
        }
    }
    
    /**
     * 네비게이션 완료 처리 (UI에서 호출)
     */
    fun onNavigationCompleted() {
        _uiState.value = _uiState.value.copy(
            navigationTarget = null
        )
    }
}
