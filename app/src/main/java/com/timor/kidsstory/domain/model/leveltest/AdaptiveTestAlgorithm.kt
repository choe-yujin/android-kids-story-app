package com.timor.kidsstory.domain.model.leveltest

/**
 * 개선된 적응형 레벨 테스트 알고리즘
 * 
 * **알고리즘 로직:**
 * 1. 레벨 3에서 시작 (중간 레벨)
 * 2. 정답/오답에 따라 위/아래 레벨로 이동
 * 3. 연속 정답 2개 or 오답 2개 시 해당 레벨 확정
 * 4. 최대 5문제까지 출제하여 정확한 레벨 측정
 * 5. 모든 레벨 (1~5) 결과 가능하도록 설계
 */
class AdaptiveTestAlgorithm {
    private var currentLevel = 3 // 시작 레벨 (중간)
    private var questionsAnswered = 0
    private var correctAnswers = 0
    private var consecutiveCorrect = 0 // 연속 정답 수
    private var consecutiveWrong = 0 // 연속 오답 수
    private val answerHistory = mutableListOf<LevelAnswer>() // 레벨별 답변 기록
    
    data class LevelAnswer(
        val level: Int,
        val isCorrect: Boolean,
        val questionNumber: Int
    )
    
    /**
     * 답변 결과를 바탕으로 다음 단계 결정
     * 
     * @param isCorrect 현재 문제의 정답 여부
     * @return 테스트 결과 (계속/완료)
     */
    fun processAnswer(isCorrect: Boolean): TestStepResult {
        questionsAnswered++
        if (isCorrect) correctAnswers++
        
        // 답변 기록 저장
        answerHistory.add(LevelAnswer(currentLevel, isCorrect, questionsAnswered))
        
        // 연속 정답/오답 카운트 업데이트
        if (isCorrect) {
            consecutiveCorrect++
            consecutiveWrong = 0
        } else {
            consecutiveWrong++
            consecutiveCorrect = 0
        }
        
        // 조기 종료 조건 체크
        val earlyFinish = checkEarlyFinishConditions()
        if (earlyFinish != null) {
            return earlyFinish
        }
        
        // 최대 문제 수 도달 시 종료
        if (questionsAnswered >= MAX_QUESTIONS) {
            return TestStepResult.Finished(calculateFinalLevel())
        }
        
        // 다음 레벨 결정
        val nextLevel = determineNextLevel(isCorrect)
        currentLevel = nextLevel
        
        return TestStepResult.Continue(nextLevel)
    }
    
    /**
     * 조기 종료 조건 체크
     * - 연속 2회 정답: 현재 레벨보다 높은 레벨 추천
     * - 연속 2회 오답: 현재 레벨보다 낮은 레벨 추천
     * - 특정 패턴 감지 시 적절한 레벨 추천
     */
    private fun checkEarlyFinishConditions(): TestStepResult.Finished? {
        // 최소 2문제는 풀어야 함
        if (questionsAnswered < 2) return null
        
        // 연속 2회 정답 → 현재 레벨 적합하거나 더 높은 레벨
        if (consecutiveCorrect >= 2) {
            val recommendedLevel = when {
                currentLevel >= 5 -> 5 // 최대 레벨
                questionsAnswered <= 2 -> currentLevel + 1 // 초반에는 한 단계 상승
                else -> currentLevel // 안정적인 레벨
            }
            return TestStepResult.Finished(recommendedLevel)
        }
        
        // 연속 2회 오답 → 현재 레벨보다 낮은 레벨
        if (consecutiveWrong >= 2) {
            val recommendedLevel = when {
                currentLevel <= 1 -> 1 // 최소 레벨
                questionsAnswered <= 2 -> maxOf(1, currentLevel - 2) // 초반 실수는 큰 폭 하락
                else -> maxOf(1, currentLevel - 1) // 한 단계 하락
            }
            return TestStepResult.Finished(recommendedLevel)
        }
        
        // 특별한 패턴 감지
        if (questionsAnswered >= 3) {
            return checkSpecialPatterns()
        }
        
        return null
    }
    
    /**
     * 특별한 답변 패턴을 분석하여 적절한 레벨 결정
     */
    private fun checkSpecialPatterns(): TestStepResult.Finished? {
        if (answerHistory.size < 3) return null
        
        val recent3 = answerHistory.takeLast(3)
        val correctCount = recent3.count { it.isCorrect }
        val levels = recent3.map { it.level }.distinct()
        
        // 다양한 레벨에서 2/3 정답 → 안정적인 중간 레벨
        if (correctCount == 2 && levels.size >= 2) {
            val avgLevel = recent3.map { it.level }.average().toInt()
            return TestStepResult.Finished(avgLevel.coerceIn(1, 5))
        }
        
        // 한 레벨에서 일관된 결과
        if (levels.size == 1) {
            val level = levels.first()
            when (correctCount) {
                3 -> return TestStepResult.Finished(minOf(5, level + 1)) // 3개 정답 → 상승
                0 -> return TestStepResult.Finished(maxOf(1, level - 1)) // 3개 오답 → 하락
                1 -> return TestStepResult.Finished(maxOf(1, level - 1)) // 대부분 틀림 → 하락
                2 -> return TestStepResult.Finished(level) // 적절한 난이도
            }
        }
        
        return null
    }
    
    /**
     * 다음 레벨 결정 로직
     */
    private fun determineNextLevel(isCorrect: Boolean): Int {
        return when {
            // 첫 문제 결과에 따른 분기
            questionsAnswered == 1 -> {
                if (isCorrect) 4 else 2
            }
            
            // 두 번째 문제 이후 세밀한 조정
            isCorrect -> {
                when {
                    currentLevel >= 5 -> 5 // 최대 레벨 유지
                    consecutiveCorrect >= 1 -> minOf(5, currentLevel + 1) // 정답 시 상승
                    else -> currentLevel
                }
            }
            
            else -> {
                when {
                    currentLevel <= 1 -> 1 // 최소 레벨 유지
                    consecutiveWrong >= 1 -> maxOf(1, currentLevel - 1) // 오답 시 하락
                    else -> currentLevel
                }
            }
        }
    }
    
    /**
     * 최종 레벨 계산 (모든 조건을 종합)
     */
    private fun calculateFinalLevel(): Int {
        if (answerHistory.isEmpty()) return 3
        
        val totalCorrect = correctAnswers
        val totalQuestions = questionsAnswered
        val accuracy = totalCorrect.toFloat() / totalQuestions
        
        // 정확도 기반 기본 레벨
        val baseLevel = when {
            accuracy >= 0.8f -> 4 // 80% 이상
            accuracy >= 0.6f -> 3 // 60% 이상
            accuracy >= 0.4f -> 2 // 40% 이상
            else -> 1
        }
        
        // 최근 성과와 현재 레벨을 고려하여 미세 조정
        val recentPerformance = answerHistory.takeLast(2)
        val recentCorrect = recentPerformance.count { it.isCorrect }
        
        val adjustedLevel = when {
            recentCorrect == 2 && currentLevel >= baseLevel -> minOf(5, baseLevel + 1)
            recentCorrect == 0 && currentLevel <= baseLevel -> maxOf(1, baseLevel - 1)
            else -> baseLevel
        }
        
        return adjustedLevel.coerceIn(1, 5)
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
     * 정확도 반환 (0.0 ~ 1.0)
     */
    fun getAccuracy(): Float = if (questionsAnswered > 0) correctAnswers.toFloat() / questionsAnswered else 0f
    
    /**
     * 테스트 진행 상황 요약
     */
    fun getTestSummary(): TestSummary {
        return TestSummary(
            questionsAnswered = questionsAnswered,
            correctAnswers = correctAnswers,
            currentLevel = currentLevel,
            consecutiveCorrect = consecutiveCorrect,
            consecutiveWrong = consecutiveWrong,
            accuracy = getAccuracy()
        )
    }
    
    /**
     * 테스트 알고리즘 초기화
     */
    fun reset() {
        currentLevel = 3
        questionsAnswered = 0
        correctAnswers = 0
        consecutiveCorrect = 0
        consecutiveWrong = 0
        answerHistory.clear()
    }
    
    companion object {
        private const val MAX_QUESTIONS = 3 // 최대 문제 수 (3문제)
    }
}

/**
 * 테스트 진행 상황 요약
 */
data class TestSummary(
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val currentLevel: Int,
    val consecutiveCorrect: Int,
    val consecutiveWrong: Int,
    val accuracy: Float
)

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
