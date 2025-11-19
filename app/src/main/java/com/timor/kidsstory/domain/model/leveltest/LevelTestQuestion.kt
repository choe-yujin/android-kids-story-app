package com.timor.kidsstory.domain.model.leveltest

/**
 * 레벨 테스트 질문 도메인 모델
 */
data class LevelTestQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
) {
    /**
     * 선택한 답변이 정답인지 확인
     */
    fun isCorrectAnswer(selectedIndex: Int): Boolean {
        return selectedIndex == correctAnswerIndex
    }
}
