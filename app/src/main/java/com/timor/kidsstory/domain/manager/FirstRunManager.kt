package com.timor.kidsstory.domain.manager

import android.util.Log
import com.timor.kidsstory.domain.manager.questionbank.QuestionBankManager
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 첫 실행 관리자
 * - 앱 첫 실행 여부 감지
 * - 언어 선택 및 레벨 테스트 완료 후 설정 저장
 * - 질문 은행 초기화 연동
 */
@Singleton
class FirstRunManager @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val questionBankManager: QuestionBankManager
) {
    
    /**
     * 앱이 첫 실행인지 확인
     * @return true: 첫 실행, false: 재실행
     */
    suspend fun isFirstRun(): Boolean {
        val prefs = userPreferenceRepository.getUserPreferences().first()
        val isFirstRun = prefs.isFirstRun
        Log.d(TAG, "🔍 Checking first run status: $isFirstRun")
        return isFirstRun
    }
    
    /**
     * 저장된 언어와 레벨 정보 가져오기
     * @return Pair<언어코드, 레벨> 또는 null (설정되지 않은 경우)
     */
    suspend fun getSelectedLanguageAndLevel(): Pair<String, Int>? {
        val prefs = userPreferenceRepository.getUserPreferences().first()
        
        return if (prefs.languageCode.isNotEmpty()) {
            Pair(prefs.languageCode, prefs.selectedLevel)
        } else {
            // If isFirstRun is false but languageCode is empty, it's an inconsistent state.
            // Provide a default to prevent looping back to LanguageSelection.
            if (!prefs.isFirstRun) {
                Log.w(TAG, "⚠️ Inconsistent state: isFirstRun is false but languageCode is empty. Providing default.")
                Pair(DEFAULT_LANGUAGE_CODE, DEFAULT_LEVEL) // Provide a default
            } else {
                Log.d(TAG, "📝 No language/level settings found (first run)")
                null
            }
        }
    }
    
    /**
     * 첫 실행 완료 처리
     * - 질문 은행 초기화 (assets → 내부저장소)
     * - 사용자 설정 저장 (언어, 레벨, 첫실행 완료)
     * 
     * @param language 선택된 언어
     * @param level 측정된 또는 기본 레벨
     * @param hasCompletedLevelTest 레벨 테스트 완료 여부
     */
    suspend fun completeFirstRun(
        language: String, 
        level: Int, 
        hasCompletedLevelTest: Boolean = false
    ) {
        Log.d(TAG, "🎉 Completing first run - language: $language, level: $level, testCompleted: $hasCompletedLevelTest")
        
        try {
            // 1. 질문 은행 초기화 (assets → 내부저장소)
            questionBankManager.initializeQuestionBanks()
            
            // 2. 사용자 설정 저장
            userPreferenceRepository.updateLanguageAndLevel(
                languageCode = language,
                level = level,
                hasCompletedLevelTest = hasCompletedLevelTest
            )
            
            Log.d(TAG, "✅ First run completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to complete first run", e)
            throw e
        }
    }
    
    /**
     * 레벨 테스트 건너뛰기 처리
     * - 선택된 언어로 기본 레벨(3) 설정
     * 
     * @param language 선택된 언어
     */
    suspend fun skipLevelTest(language: String) {
        Log.d(TAG, "⏭️ Skipping level test for language: $language")
        completeFirstRun(
            language = language,
            level = DEFAULT_LEVEL,
            hasCompletedLevelTest = false
        )
    }
    
    /**
     * 레벨 테스트 완료 처리
     * - 측정된 레벨로 설정
     * 
     * @param language 선택된 언어
     * @param measuredLevel 테스트를 통해 측정된 레벨
     */
    suspend fun completeLevelTest(language: String, measuredLevel: Int) {
        Log.d(TAG, "🎯 Level test completed - language: $language, level: $measuredLevel")
        completeFirstRun(
            language = language,
            level = measuredLevel,
            hasCompletedLevelTest = true
        )
    }
    
    /**
     * 첫 실행 상태만 업데이트 (긴급시 사용)
     */
    suspend fun markFirstRunComplete() {
        userPreferenceRepository.markFirstRunComplete()
        Log.d(TAG, "✅ Marked first run as complete")
    }
    
    companion object {
        private const val TAG = "FirstRunManager"
        private const val DEFAULT_LEVEL = 3
        private const val DEFAULT_LANGUAGE_CODE = "en" // 기본 언어를 en으로 원복
    }
}
