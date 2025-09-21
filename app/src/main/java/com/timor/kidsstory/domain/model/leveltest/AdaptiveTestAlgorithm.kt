package com.timor.kidsstory.domain.model.leveltest

/**
 * 적응형 레벨 테스트 알고리즘
 * Level 3부터 시작하여 정답/오답에 따라 레벨을 조정
 */
class AdaptiveTestAlgorithm {
    private var currentLevel = 3 // 시작 레벨
    private var questionsAnswered = 0
    private var correctAnswers = 0
    
    /**
     * 답변 결과를 바탕으로 다음 단계 결정
     * 
     * @param isCorrect 현재 문제의 정답 여부
     * @return 테스트 결과 (계속/완료)
     */
    fun processAnswer(isCorrect: Boolean): TestStepResult {
        questionsAnswered++
        if (isCorrect) correctAnswers++
        
        return when {
            // 레벨 3에서 맞춤 → 레벨 4로
            currentLevel == 3 && isCorrect -> {
                currentLevel = 4
                TestStepResult.Continue(4)
            }
            // 레벨 3에서 틀림 → 레벨 2로
            currentLevel == 3 && !isCorrect -> {
                currentLevel = 2
                TestStepResult.Continue(2)
            }
            // 레벨 4에서 맞춤 → 레벨 5로
            currentLevel == 4 && isCorrect -> {
                currentLevel = 5
                TestStepResult.Continue(5)
            }
            // 레벨 4에서 틀림 → 레벨 3 추천
            currentLevel == 4 && !isCorrect -> {
                TestStepResult.Finished(recommendedLevel = 3)
            }
            // 레벨 5에서 맞춤 → 레벨 5 추천
            currentLevel == 5 && isCorrect -> {
                TestStepResult.Finished(recommendedLevel = 5)
            }
            // 레벨 5에서 틀림 → 레벨 4 추천
            currentLevel == 5 && !isCorrect -> {
                TestStepResult.Finished(recommendedLevel = 4)
            }
            // 레벨 2에서 맞춤 → 레벨 2 추천
            currentLevel == 2 && isCorrect -> {
                TestStepResult.Finished(recommendedLevel = 2)
            }
            // 레벨 2에서 틀림 → 레벨 1로
            currentLevel == 2 && !isCorrect -> {
                currentLevel = 1
                TestStepResult.Continue(1)
            }
            // 레벨 1에서 맞춤 → 레벨 1 추천
            currentLevel == 1 && isCorrect -> {
                TestStepResult.Finished(recommendedLevel = 1)
            }
            // 레벨 1에서 틀림 → 레벨 1 추천 (최하위)
            currentLevel == 1 && !isCorrect -> {
                TestStepResult.Finished(recommendedLevel = 1)
            }
            else -> TestStepResult.Finished(recommendedLevel = 1)
        }
    }
    
    /**
     * 현재 레벨 반환
     */
    fun getCurrentLevel(): Int = currentLevel
    
    /**
     * 현재까지 답변한 문제 수
     */
    fun getQuestionsAnswered(): Int = questionsAnswered
    
    /**
     * 현재까지 맞춘 문제 수
     */
    fun getCorrectAnswers(): Int = correctAnswers
    
    /**
     * 테스트 알고리즘 초기화
     */
    fun reset() {
        currentLevel = 3
        questionsAnswered = 0
        correctAnswers = 0
    }
}

/**
 * 테스트 단계 결과
 */
sealed class TestStepResult {
    /**
     * 테스트 계속 - 다음 레벨 문제 출제
     * @property nextLevel 다음에 출제할 레벨
     */
    data class Continue(val nextLevel: Int) : TestStepResult()
    
    /**
     * 테스트 완료 - 최종 추천 레벨 결정
     * @property recommendedLevel 최종 추천 레벨
     */
    data class Finished(val recommendedLevel: Int) : TestStepResult()
}
