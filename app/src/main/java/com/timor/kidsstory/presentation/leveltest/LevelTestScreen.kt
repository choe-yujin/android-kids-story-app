package com.timor.kidsstory.presentation.leveltest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.FontPolicy
import com.timor.kidsstory.ui.components.LocalizedText

/**
 * 레벨 테스트 화면 Root
 */
@Composable
fun LevelTestScreenRoot(
    language: String,
    onNavigateToBookshelf: (language: String, level: Int, wasSkipped: Boolean, showLevelResultPopup: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LevelTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 언어 설정 (최초 한 번만)
    LaunchedEffect(language) {
        viewModel.startLevelTest(language)
    }

    // 네비게이션 처리
    LaunchedEffect(uiState.navigationTarget) {
        when (val target = uiState.navigationTarget) {
            is LevelTestNavigationTarget.Bookshelf -> {
                onNavigateToBookshelf(target.language, target.level, target.wasSkipped, target.showLevelResultPopup)
                viewModel.onNavigationCompleted()
            }

            null -> {
                // 네비게이션 대상 없음
            }
        }
    }

    LevelTestScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

/**
 * 레벨 테스트 화면
 */
@Composable
fun LevelTestScreen(
    uiState: LevelTestUiState,
    onAction: (LevelTestAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE9ECEF)
                    )
                )
            )
    ) {
        when (uiState.testState) {
            is TestState.NotStarted -> {
                LevelTestIntroContent(
                    language = uiState.language,
                    onStartTest = { onAction(LevelTestAction.StartTest) },
                    onSkipTest = { onAction(LevelTestAction.SkipTest) },
                    isLandscape = isLandscape,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TestState.InProgress -> {
                LevelTestQuestionContent(
                    uiState = uiState,
                    onAction = onAction,
                    isLandscape = isLandscape,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TestState.Completed -> {
                LevelTestCompletedContent(
                    finalLevel = uiState.finalLevel ?: 3,
                    language = uiState.language,
                    onStartReading = { onAction(LevelTestAction.StartReading) },
                    onRetakeTest = { onAction(LevelTestAction.RestartTest) },
                    isLandscape = isLandscape,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TestState.Skipped -> {
                // 스킵된 경우는 바로 네비게이션 되므로 빈 화면
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        // 로딩 오버레이
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 에러 메시지
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 레벨 테스트 시작 화면
 */
@Composable
private fun LevelTestIntroContent(
    language: String,
    onStartTest: () -> Unit,
    onSkipTest: () -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val contentWidth = if (screenWidth >= 800) 0.6f else 1f

    Column(
        modifier = modifier
            .fillMaxWidth(contentWidth)
            .padding(
                horizontal = if (isLandscape) 64.dp else 24.dp,
                vertical = if (isLandscape) 24.dp else 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LocalizedText(
            resId = R.string.level_test_title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontPolicy = FontPolicy.PRETENDA_ALL
        )

        Spacer(modifier = Modifier.height(16.dp))

        LocalizedText(
            resId = R.string.level_test_introduction,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontPolicy = FontPolicy.PRETENDA_ALL
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartTest,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(26.dp)
        ) {
            LocalizedText(
                resId = R.string.level_test_start_button,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 18.sp,
                fontPolicy = FontPolicy.PRETENDA_ALL
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onSkipTest,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            LocalizedText(
                resId = R.string.level_test_skip_button,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                fontSize = 18.sp,
                fontPolicy = FontPolicy.PRETENDA_ALL
            )
        }
    }
}

/**
 * 레벨 테스트 문제 콘텐츠
 */
@Composable
private fun LevelTestQuestionContent(
    uiState: LevelTestUiState,
    onAction: (LevelTestAction) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val contentWidth = if (screenWidth >= 800) 0.6f else 1f
    val verticalPadding = if (screenWidth < 600) 0.dp else 32.dp
    val horizontalPadding = if (isLandscape) 64.dp else 24.dp

    Column(
        modifier = modifier
            .fillMaxWidth(contentWidth)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        // Question area
        uiState.currentQuestion?.let { question ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(
                            if (screenWidth < 600) 16.dp else 24.dp
                        )
                    ) {
                        Text(
                            text = question.question,
                            fontSize = if (screenWidth < 600) 18.sp else 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 48.dp)
                                .padding(end = 48.dp) // Make more space for skip button
                        )

                        Spacer(modifier = Modifier.height(if (screenWidth < 600) 16.dp else 24.dp))

                        // Options
                        question.options.forEachIndexed { index, option ->
                            val isSelected = uiState.selectedAnswerIndex == index
                            val isCorrect = question.isCorrectAnswer(index)
                            val showResult = uiState.showAnswerResult

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = if (screenWidth < 600) 3.dp else 4.dp)
                                    .padding(start = 48.dp) // Balance with skip button
                                    .padding(end = 48.dp), // Align with question text
                                onClick = {
                                    if (!showResult) {
                                        onAction(LevelTestAction.SelectAnswer(index))
                                    }
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        showResult && isCorrect -> Color(0xFF4CAF50) // Correct: Green
                                        showResult && isSelected && !isCorrect -> Color(0xFFF44336) // Selected wrong: Red
                                        isSelected && !showResult -> MaterialTheme.colorScheme.primaryContainer // Selected
                                        else -> MaterialTheme.colorScheme.surface // Default
                                    }
                                )
                            ) {
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(
                                        if (screenWidth < 600) 12.dp else 16.dp
                                    ),
                                    fontSize = if (screenWidth < 600) 14.sp else 16.sp,
                                    color = when {
                                        showResult && (isCorrect || (isSelected && !isCorrect)) -> Color.White
                                        isSelected && !showResult -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = { onAction(LevelTestAction.SkipTest) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 8.dp, top = 8.dp)
                    ) {
                        LocalizedText(
                            resId = R.string.level_test_skip_button,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontPolicy = FontPolicy.PRETENDA_ALL
                        )
                    }
                }
            }
        }
    }
}

/**
 * 레벨 테스트 완료 화면
 */
@Composable
private fun LevelTestCompletedContent(
    finalLevel: Int,
    language: String,
    onStartReading: () -> Unit,
    onRetakeTest: () -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val contentWidth = if (screenWidth >= 800) 0.6f else 1f

    Column(
        modifier = modifier
            .fillMaxWidth(contentWidth)
            .padding(
                horizontal = if (isLandscape) 64.dp else 24.dp,
                vertical = if (isLandscape) 24.dp else 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LocalizedText(
            resId = R.string.level_test_complete_title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontPolicy = FontPolicy.PRETENDA_ALL
        )

        Spacer(modifier = Modifier.height(16.dp))

        LocalizedText(
            resId = R.string.level_test_result_level,
            formatArgs = arrayOf(finalLevel),
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontPolicy = FontPolicy.PRETENDA_ALL
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartReading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(26.dp)
        ) {
            LocalizedText(
                resId = R.string.level_test_start_reading_button,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 18.sp,
                fontPolicy = FontPolicy.PRETENDA_ALL
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRetakeTest,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            LocalizedText(
                resId = R.string.level_test_retry_button,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                fontSize = 18.sp,
                fontPolicy = FontPolicy.PRETENDA_ALL
            )
        }
    }
}
