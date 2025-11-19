package com.timor.kidsstory.presentation.leveltest

/**
 * 레벨 테스트 화면 액션
 */
sealed interface LevelTestAction {
    /**
     * 테스트 시작
     */
    data object StartTest : LevelTestAction
    
    /**
     * 답변 선택
     * @param answerIndex 선택한 답변 인덱스 (0, 1, 2)
     */
    data class SelectAnswer(val answerIndex: Int) : LevelTestAction
    
    /**
     * 다음 문제로 진행
     */
    data object NextQuestion : LevelTestAction
    
    /**
     * 테스트 건너뛰기
     */
    data object SkipTest : LevelTestAction
    
    /**
     * 테스트 재시작
     */
    data object RestartTest : LevelTestAction
    
    /**
     * 독서 시작 (결과 화면에서)
     */
    data object StartReading : LevelTestAction
}
