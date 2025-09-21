package com.timor.kidsstory.presentation.leveltest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.manager.FirstRunManager
import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion
import com.timor.kidsstory.domain.repository.leveltest.LevelTestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 레벨 테스트 화면 ViewModel
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
        _uiState.value = _uiState.value.copy(
            testState = TestState.InProgress,
            currentLevel = 1
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
                val currentState = _uiState.value
                val question = levelTestRepository.getRandomQuestion(currentLanguage, currentState.currentLevel)
                
                if (question != null) {
                    _uiState.value = _uiState.value.copy(
                        currentQuestion = question,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    // 해당 레벨에 문제가 없음 -> 테스트 완료
                    finishLevelTest()
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
     * 답안 선택 처리
     */
    private fun handleAnswerSelection(selectedIndex: Int) {
        val currentQuestion = _uiState.value.currentQuestion ?: return
        val isCorrect = currentQuestion.isCorrectAnswer(selectedIndex)
        
        _uiState.value = _uiState.value.copy(
            selectedAnswerIndex = selectedIndex,
            isCorrect = isCorrect,
            showAnswerResult = true
        )
        
        // 1초 후 자동으로 다음 문제로 이동
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            
            if (isCorrect) {
                // 정답: 다음 레벨로
                val nextLevel = _uiState.value.currentLevel + 1
                if (nextLevel > 5) {
                    // 모든 레벨 완료
                    finishLevelTest()
                } else {
                    _uiState.value = _uiState.value.copy(currentLevel = nextLevel)
                    loadNextQuestion()
                }
            } else {
                // 오답: 현재 레벨에서 완료
                finishLevelTest()
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
                    navigationTarget = LevelTestNavigationTarget.Bookshelf(currentLanguage, 3)
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
        _uiState.value = LevelTestUiState(
            language = currentLanguage,
            testState = TestState.NotStarted
        )
    }
    
    /**
     * 레벨 테스트 완료
     */
    private fun finishLevelTest() {
        viewModelScope.launch {
            try {
                val measuredLevel = _uiState.value.currentLevel
                
                // 측정된 레벨로 FirstRun 완료 처리
                firstRunManager.completeLevelTest(currentLanguage, measuredLevel)
                
                _uiState.value = _uiState.value.copy(
                    testState = TestState.Completed,
                    isTestCompleted = true,
                    finalLevel = measuredLevel
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
        val finalLevel = _uiState.value.finalLevel ?: 3
        _uiState.value = _uiState.value.copy(
            navigationTarget = LevelTestNavigationTarget.Bookshelf(currentLanguage, finalLevel)
        )
    }
    
    /**
     * 네비게이션 완료 처리
     */
    fun onNavigationCompleted() {
        _uiState.value = _uiState.value.copy(
            navigationTarget = null
        )
    }
}
