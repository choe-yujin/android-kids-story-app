package com.timor.kidsstory.domain.util

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


/*
* 언어 변겨 이벤트 및 현재 언어 코드를 관리하는 싱글톤
* */
object LanguageManager {
    // 현재 설정된 언어코드
    private var currentLanguageCode: String = LanguageConstants.getResourceLangCode(LanguageConstants.DEFAULT_LANGUAGE.code)

    // 언어 변경 이벤트를
    private val _languageChangeFlow = MutableSharedFlow<String>(replay = 1)
    val languageChangeFlow: SharedFlow<String> = _languageChangeFlow.asSharedFlow()

    // 현재 설정된 언어코드 반환
    fun getCurrentLanguageCode(): String = currentLanguageCode

    /**
     * 언어 코드 업데이트 및 변경 이벤트 발행
     *
     */
    suspend fun updateLanguageCode(newLanguageCode: String) {
        // 비어있는 경우 기본값 사용
        val codeToUse = newLanguageCode.ifEmpty { LanguageConstants.DEFAULT_LANGUAGE.code }
        val newString = LanguageConstants.getResourceLangCode(codeToUse)
        Logger.e("언어 newString: $newString")
        _languageChangeFlow.emit(newString)
    }
}