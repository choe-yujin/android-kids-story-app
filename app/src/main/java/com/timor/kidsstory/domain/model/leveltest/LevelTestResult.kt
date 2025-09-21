package com.timor.kidsstory.domain.model.leveltest

/**
 * 레벨 테스트 결과 도메인 모델
 * 
 * @property recommendedLevel 추천 읽기 레벨 (1-5)
 * @property totalQuestions 총 문제 수
 * @property correctAnswers 정답 수
 * @property language 테스트한 언어
 */
data class LevelTestResult(
    val recommendedLevel: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val language: String
) {
    init {
        require(recommendedLevel in 1..5) { "Recommended level must be between 1 and 5" }
        require(totalQuestions >= 0) { "Total questions must be non-negative" }
        require(correctAnswers >= 0) { "Correct answers must be non-negative" }
        require(correctAnswers <= totalQuestions) { "Correct answers cannot exceed total questions" }
    }
    
    /**
     * 정답률 계산 (0.0 ~ 1.0)
     */
    val accuracy: Float = if (totalQuestions > 0) {
        correctAnswers.toFloat() / totalQuestions
    } else {
        0f
    }
    
    /**
     * 정답률 퍼센트 (0 ~ 100)
     */
    val accuracyPercentage: Int = (accuracy * 100).toInt()
}
