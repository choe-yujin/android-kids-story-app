package com.timor.kidsstory.presentation.leveltest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.manager.FirstRunManager
import com.timor.kidsstory.domain.model.leveltest.AdaptiveTestAlgorithm
import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion
import com.timor.kidsstory.domain.model.leveltest.TestStepResult
import com.timor.kidsstory.domain.repository.leveltest.LevelTestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 레벨 테스트 화면 ViewModel (개선된 알고리즘 적용)
 */
@HiltViewModel
class LevelTestViewModel @Inject constructor(
    private val levelTestRepository: LevelTestRepository,
    private val firstRunManager: FirstRunManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LevelTestUiState())
    val uiState: StateFlow<LevelTestUiState> = _uiState.asStateFlow()
    
    // 현재 언어
    private var currentLanguage: String = ""
    
    // 개선된 적응형 알고리즘
    private val testAlgorithm = AdaptiveTestAlgorithm()
    
    /**
     * 레벨 테스트 시작
     */
    fun startLevelTest(language: String) {
        currentLanguage = language
        _uiState.value = _uiState.value.copy(
            language = language,
            testState = TestState.NotStarted
        )
    }
    
    /**
     * 액션 처리
     */
    fun onAction(action: LevelTestAction) {
        when (action) {
            is LevelTestAction.StartTest -> {
                startActualTest()
            }
            is LevelTestAction.SelectAnswer -> {
                handleAnswerSelection(action.answerIndex)
            }
            is LevelTestAction.NextQuestion -> {
                loadNextQuestion()
            }
            is LevelTestAction.SkipTest -> {
                skipLevelTest()
            }
            is LevelTestAction.RestartTest -> {
                restartTest()
            }
            is LevelTestAction.StartReading -> {
                startReading()
            }
        }
    }
    
    /**
     * 실제 테스트 시작
     */
    private fun startActualTest() {
        testAlgorithm.reset() // 알고리즘 초기화
        
        _uiState.value = _uiState.value.copy(
            testState = TestState.InProgress,
            currentLevel = testAlgorithm.getCurrentLevel(),
            currentQuestionNumber = 1
        )
        loadNextQuestion()
    }
    
    /**
     * 다음 문제 로드
     */
    private fun loadNextQuestion() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                selectedAnswerIndex = null,
                showAnswerResult = false
            )
            
            try {
                val currentLevel = testAlgorithm.getCurrentLevel()
                val question = levelTestRepository.getRandomQuestion(currentLanguage, currentLevel)
                
                if (question != null) {
                    _uiState.value = _uiState.value.copy(
                        currentQuestion = question,
                        currentLevel = currentLevel,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    // 해당 레벨에 문제가 없음 -> 다른 레벨 시도 또는 테스트 완료
                    handleNoQuestionAvailable()
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load question"
                )
            }
        }
    }
    
    /**
     * 해당 레벨에 문제가 없을 때 처리
     */
    private fun handleNoQuestionAvailable() {
        // 다른 레벨에서 문제 찾기 시도
        val alternativeLevels = listOf(3, 2, 4, 1, 5) // 우선순위 순서
        
        viewModelScope.launch {
            for (level in alternativeLevels) {
                val question = levelTestRepository.getRandomQuestion(currentLanguage, level)
                if (question != null) {
                    // 대체 레벨 문제 발견
                    _uiState.value = _uiState.value.copy(
                        currentQuestion = question,
                        currentLevel = level,
                        isLoading = false,
                        errorMessage = null
                    )
                    return@launch
                }
            }
            
            // 모든 레벨에서 문제를 찾을 수 없음 -> 테스트 완료
            finishLevelTest(3) // 기본 레벨로 완료
        }
    }
    
    /**
     * 답안 선택 처리 (개선된 알고리즘 사용)
     */
    private fun handleAnswerSelection(selectedIndex: Int) {
        val currentQuestion = _uiState.value.currentQuestion ?: return
        val isCorrect = currentQuestion.isCorrectAnswer(selectedIndex)

        _uiState.value = _uiState.value.copy(
            selectedAnswerIndex = selectedIndex,
            isCorrect = isCorrect,
            showAnswerResult = true
        )

        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // 결과 표시를 위한 지연

            // 개선된 알고리즘으로 다음 단계 결정
            when (val result = testAlgorithm.processAnswer(isCorrect)) {
                is TestStepResult.Continue -> {
                    // 테스트 계속 -> 다음 문제 로드
                    val summary = testAlgorithm.getTestSummary()
                    _uiState.value = _uiState.value.copy(
                        currentQuestionNumber = summary.questionsAnswered + 1,
                        currentLevel = result.nextLevel
                    )
                    loadNextQuestion()
                }
                
                is TestStepResult.Finished -> {
                    // 테스트 완료
                    finishLevelTest(result.recommendedLevel)
                }
            }
        }
    }
    
    /**
     * 레벨 테스트 스킵
     */
    private fun skipLevelTest() {
        viewModelScope.launch {
            try {
                // 기본 레벨(3)로 설정
                firstRunManager.skipLevelTest(currentLanguage)
                
                _uiState.value = _uiState.value.copy(
                    testState = TestState.Skipped,
                    navigationTarget = LevelTestNavigationTarget.Bookshelf(currentLanguage, 3, true)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to skip level test"
                )
            }
        }
    }
    
    /**
     * 테스트 재시작
     */
    private fun restartTest() {
        testAlgorithm.reset()
        _uiState.value = LevelTestUiState(
            language = currentLanguage,
            testState = TestState.NotStarted
        )
    }
    
    /**
     * 레벨 테스트 완료
     */
    private fun finishLevelTest(recommendedLevel: Int) {
        viewModelScope.launch {
            try {
                firstRunManager.completeLevelTest(currentLanguage, recommendedLevel)
                firstRunManager.markFirstRunComplete() // Explicitly mark first run as complete
                
                val summary = testAlgorithm.getTestSummary()
                
                _uiState.value = _uiState.value.copy(
                    testState = TestState.Completed,
                    isTestCompleted = true,
                    finalLevel = recommendedLevel,
                    // 테스트 결과 상세 정보 추가
                    currentQuestionNumber = summary.questionsAnswered
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to complete level test"
                )
            }
        }
    }
    
    /**
     * 독서 시작 (테스트 완료 후)
     */
    private fun startReading() {
        viewModelScope.launch {
            firstRunManager.markFirstRunComplete() // Explicitly mark first run as complete
            val finalLevel = _uiState.value.finalLevel ?: 3
            _uiState.value = _uiState.value.copy(
                navigationTarget = LevelTestNavigationTarget.Bookshelf(currentLanguage, finalLevel, false, true)
            )
        }
    }
    
    /**
     * 네비게이션 완료 처리
     */
    fun onNavigationCompleted() {
        _uiState.value = _uiState.value.copy(
            navigationTarget = null
        )
    }
    
    /**
     * 현재 테스트 진행 상황 가져오기 (디버깅용)
     */
    fun getTestSummary() = testAlgorithm.getTestSummary()
}
