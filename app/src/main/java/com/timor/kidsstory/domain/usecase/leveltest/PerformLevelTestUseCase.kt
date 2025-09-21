package com.timor.kidsstory.domain.usecase.leveltest

import com.timor.kidsstory.domain.model.leveltest.LevelTestResult
import javax.inject.Inject

/**
 * 레벨 테스트 수행 UseCase
 * 간단한 레벨 테스트 로직을 제공
 */
class PerformLevelTestUseCase @Inject constructor() {
    
    /**
     * 최종 테스트 결과 생성
     * 
     * @param language 테스트한 언어
     * @param finalLevel 최종 레벨
     * @param totalQuestions 총 문제 수
     * @param correctAnswers 정답 수
     * @return 테스트 결과
     */
    fun createFinalResult(
        language: String, 
        finalLevel: Int,
        totalQuestions: Int = 1,
        correctAnswers: Int = 1
    ): LevelTestResult {
        return LevelTestResult(
            recommendedLevel = finalLevel,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            language = language
        )
    }
    
    /**
     * 스킵 시 기본 결과 생성 (레벨 3 추천)
     * 
     * @param language 테스트한 언어
     * @return 기본 테스트 결과
     */
    fun createSkippedResult(language: String): LevelTestResult {
        return LevelTestResult(
            recommendedLevel = 3, // 기본 레벨
            totalQuestions = 0,
            correctAnswers = 0,
            language = language
        )
    }
}
