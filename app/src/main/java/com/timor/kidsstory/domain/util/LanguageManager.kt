package com.timor.kidsstory.domain.util

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.timor.kidsstory.domain.model.Language

/*
* 언어 변경 이벤트 및 현재 언어 코드를 관리하는 싱글톤
* */
object LanguageManager {
    // 현재 설정된 언어코드 (상태로 관리)
    private var _currentLanguageCode by mutableStateOf(LanguageConstants.getResourceLangCode(LanguageConstants.DEFAULT_LANGUAGE.code))

    // 현재 설정된 언어코드 반환
    fun getCurrentLanguageCode(): String = _currentLanguageCode

    // 언어 코드 설정 함수 추가
    fun setCurrentLanguageCode(code: String) {
        _currentLanguageCode = LanguageConstants.getResourceLangCode(code)
    }
    
    // 지원되는 언어 목록 반환
    fun getSupportedLanguages(): List<Language> {
        return LanguageConstants.SUPPORTED_LANGUAGES
    }
}