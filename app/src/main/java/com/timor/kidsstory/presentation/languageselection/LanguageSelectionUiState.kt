package com.timor.kidsstory.presentation.languageselection

import com.timor.kidsstory.domain.model.Language

/**
 * 언어 선택 화면 UI 상태
 */
data class LanguageSelectionUiState(
    /**
     * 사용 가능한 언어 목록
     */
    val availableLanguages: List<Language> = emptyList(),
    
    /**
     * 선택된 언어
     */
    val selectedLanguage: Language? = null,
    
    /**
     * 로딩 상태
     */
    val isLoading: Boolean = true,
    
    /**
     * 에러 메시지
     */
    val errorMessage: String? = null,
    
    /**
     * 네비게이션 대상
     */
    val navigationTarget: LanguageSelectionNavigationTarget? = null
)

/**
 * 언어 선택 후 네비게이션 대상
 */
sealed class LanguageSelectionNavigationTarget {
    /**
     * 레벨 테스트로 (인터넷 사용 가능)
     */
    data class LevelTest(val language: String) : LanguageSelectionNavigationTarget()
    
    /**
     * 책장으로 (오프라인 또는 레벨 테스트 스킵)
     */
    data class Bookshelf(val language: String, val level: Int) : LanguageSelectionNavigationTarget()
}
