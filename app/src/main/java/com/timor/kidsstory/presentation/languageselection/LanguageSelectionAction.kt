package com.timor.kidsstory.presentation.languageselection

/**
 * 언어 선택 화면 액션
 */
sealed interface LanguageSelectionAction {
    /**
     * 언어 선택
     */
    data class OnLanguageSelected(val languageCode: String) : LanguageSelectionAction
}
