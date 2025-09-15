package com.timor.kidsstory.domain.usecase.language

import android.util.Log
import com.timor.kidsstory.domain.model.Language
import com.timor.kidsstory.domain.usecase.preference.SaveUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageManager
import javax.inject.Inject

/**
 * 앱 언어 변경을 처리하는 UseCase
 * 
 * 기존 BookshelfViewModel의 changeLanguage() 로직을 Domain Layer로 이동
 * - 사용자 선택 언어 저장
 * - 시스템 언어 설정 업데이트
 * - 언어 변경 유효성 검사
 */
class ChangeLanguageUseCase @Inject constructor(
    private val saveUserPreferenceUseCase: SaveUserPreferenceUseCase
) {
    /**
     * 앱 언어 변경 처리
     * 
     * @param language 변경할 언어
     * @return 언어 변경 성공 여부
     */
    suspend operator fun invoke(language: Language): Result<LanguageChangeResult> {
        return try {
            Log.d("ChangeLanguageUseCase", "Changing language to: ${language.code}")
            
            // 1. 현재 언어와 같은지 확인
            val currentLanguageCode = LanguageManager.getCurrentLanguageCode()
            if (language.code == currentLanguageCode) {
                Log.d("ChangeLanguageUseCase", "Language is already set to: ${language.code}")
                return Result.success(
                    LanguageChangeResult(
                        previousLanguage = language.code,
                        newLanguage = language.code,
                        isChanged = false,
                        shouldReloadBooks = false
                    )
                )
            }
            
            // 2. 이전 언어 코드 저장
            val previousLanguageCode = currentLanguageCode
            
            // 3. 사용자 선택사항 저장
            saveUserPreferenceUseCase.updateLanguage(language.code)
            
            // 4. 시스템 언어 설정 업데이트
            LanguageManager.setCurrentLanguageCode(language.code)
            
            val result = LanguageChangeResult(
                previousLanguage = previousLanguageCode,
                newLanguage = language.code,
                isChanged = true,
                shouldReloadBooks = true
            )
            
            Log.d("ChangeLanguageUseCase", 
                "Language changed successfully: $previousLanguageCode -> ${language.code}")
            
            Result.success(result)
            
        } catch (e: Exception) {
            Log.e("ChangeLanguageUseCase", "Error changing language to: ${language.code}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 현재 설정된 언어 코드 조회
     * 
     * @return 현재 언어 코드
     */
    fun getCurrentLanguageCode(): String {
        return LanguageManager.getCurrentLanguageCode()
    }
    
    /**
     * 지원되는 언어 목록 조회
     * 
     * @return 지원 언어 목록
     */
    fun getSupportedLanguages(): List<Language> {
        return LanguageManager.getSupportedLanguages()
    }
}

/**
 * 언어 변경 결과 정보
 * 
 * @property previousLanguage 이전 언어 코드
 * @property newLanguage 새 언어 코드
 * @property isChanged 실제로 변경되었는지 여부
 * @property shouldReloadBooks 책 목록 재로드가 필요한지 여부
 */
data class LanguageChangeResult(
    val previousLanguage: String,
    val newLanguage: String,
    val isChanged: Boolean,
    val shouldReloadBooks: Boolean
) {
    /**
     * 언어 변경 요약 메시지
     */
    val summaryMessage: String
        get() = if (isChanged) {
            "Language changed from $previousLanguage to $newLanguage"
        } else {
            "Language is already set to $newLanguage"
        }
}