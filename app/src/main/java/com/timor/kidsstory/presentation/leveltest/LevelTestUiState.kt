package com.timor.kidsstory.presentation.leveltest

import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion
import com.timor.kidsstory.domain.model.leveltest.LevelTestResult

/**
 * 레벨 테스트 화면 UI 상태
 */
data class LevelTestUiState(
    /**
     * 테스트 진행 상태
     */
    val testState: TestState = TestState.NotStarted,
    
    /**
     * 현재 문제
     */
    val currentQuestion: LevelTestQuestion? = null,
    
    /**
     * 현재 문제 번호 (1부터 시작)
     */
    val currentQuestionNumber: Int = 0,
    
    /**
     * 현재 레벨
     */
    val currentLevel: Int = 3,
    
    /**
     * 선택된 답변 인덱스 (null = 선택 안됨)
     */
    val selectedAnswerIndex: Int? = null,
    
    /**
     * 정답 여부 (null = 아직 확인 안됨)
     */
    val isCorrect: Boolean? = null,
    
    /**
     * 답변 확인 후 상태
     */
    val showAnswerResult: Boolean = false,
    
    /**
     * 테스트 결과
     */
    val testResult: LevelTestResult? = null,
    
    /**
     * 테스트 완료 여부
     */
    val isTestCompleted: Boolean = false,
    
    /**
     * 최종 측정된 레벨
     */
    val finalLevel: Int? = null,
    
    /**
     * 테스트 언어
     */
    val language: String = "en",
    
    /**
     * 로딩 상태
     */
    val isLoading: Boolean = false,
    
    /**
     * 에러 메시지
     */
    val errorMessage: String? = null,
    
    /**
     * 네비게이션 대상
     */
    val navigationTarget: LevelTestNavigationTarget? = null
)

/**
 * 레벨 테스트 네비게이션 대상
 */
sealed class LevelTestNavigationTarget {
    /**
     * 책장으로 (테스트 완료 또는 스킵)
     */
    data class Bookshelf(val language: String, val level: Int, val wasSkipped: Boolean, val showLevelResultPopup: Boolean = false) : LevelTestNavigationTarget()
}

/**
 * 테스트 진행 상태
 */
sealed class TestState {
    /**
     * 시작 전
     */
    data object NotStarted : TestState()
    
    /**
     * 진행 중
     */
    data object InProgress : TestState()
    
    /**
     * 완료
     */
    data object Completed : TestState()
    
    /**
     * 건너뛰기됨
     */
    data object Skipped : TestState()
}
