package com.timor.kidsstory.domain.util


/*
* 언어 변겨 이벤트 및 현재 언어 코드를 관리하는 싱글톤
* */
object LanguageManager {
    // 현재 설정된 언어코드
    private var currentLanguageCode: String = LanguageConstants.getResourceLangCode(LanguageConstants.DEFAULT_LANGUAGE.code)

    // 현재 설정된 언어코드 반환
    fun getCurrentLanguageCode(): String = currentLanguageCode

    // 언어 코드 설정 함수 추가
    fun setCurrentLanguageCode(code: String) {
        currentLanguageCode = LanguageConstants.getResourceLangCode(code)
    }
}