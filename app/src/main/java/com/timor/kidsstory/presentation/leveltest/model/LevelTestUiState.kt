package com.timor.kidsstory.presentation.leveltest.model

import com.timor.kidsstory.domain.model.leveltest.LevelTestQuestion

data class LevelTestUiState(
    val language: String = "en",
    val testState: TestState = TestState.NotStarted,
    val currentQuestion: LevelTestQuestion? = null,
    val currentLevel: Int = 1,
    val currentQuestionNumber: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isCorrect: Boolean = false,
    val showAnswerResult: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTestCompleted: Boolean = false,
    val finalLevel: Int? = null,
    val navigationTarget: LevelTestNavigationTarget? = null
)

sealed interface TestState {
    object NotStarted : TestState
    object InProgress : TestState
    data class Completed(val finalLevel: Int) : TestState
    object Skipped : TestState
}
