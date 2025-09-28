package com.timor.kidsstory.presentation.leveltest

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.FontPolicy
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles

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
                onNavigateToBookshelf(
                    target.language,
                    target.level,
                    target.wasSkipped,
                    target.showLevelResultPopup
                )
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
 * 레벨 테스트 화면 - 앱 톤앤매너에 맞게 리디자인
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
            .background(AppColors.primary50) // 🎨 앱의 기본 배경 색상 사용
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
                    modifier = Modifier.fillMaxSize() // 🔧 전체 화면 사용하도록 변경
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
                    .background(AppColors.neutralBlack.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AppColors.unknown500 // 🎨 앱의 노란색 사용
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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.red600.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = AppColors.red600,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 레벨 테스트 시작 화면 - 앱 스타일에 맞게 리디자인
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
    val contentWidth = when {
        screenWidth >= 800 -> 0.6f // Tablet
        isLandscape -> 0.8f // Phone landscape
        else -> 1f // Phone portrait
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AppColors.primary50, AppColors.primary100)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(contentWidth)
                .padding(
                    horizontal = if (isLandscape) 32.dp else 24.dp, // 🎨 가로 패딩 감소
                    vertical = if (isLandscape) 16.dp else 32.dp // 🎨 세로 패딩 감소
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp) // 🎨 로고 크기 감소 (가로 모드 고려)
            )
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 32.dp)) // 🎨 스페이서 감소
            LocalizedText(
                resId = R.string.level_test_title,
                style = AppTextStyles.cookieRunBlackRegular.copy(fontSize = if (isLandscape) 28.sp else 36.sp), // 🎨 폰트 크기 감소
                color = AppColors.neutral800,
                fontPolicy = FontPolicy.DEFAULT
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp)) // 🎨 스페이서 감소

            LocalizedText(
                resId = R.string.level_test_introduction,
                style = AppTextStyles.cookieRunRegular.copy(fontSize = if (isLandscape) 16.sp else 18.sp), // 🎨 폰트 크기 감소
                color = AppColors.neutral600,
                textAlign = TextAlign.Center,
                fontPolicy = FontPolicy.DEFAULT
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 48.dp)) // 🎨 스페이서 감소

            Button(
                onClick = onStartTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp), // 🎨 버튼 높이 감소 (가로 모드 고려)
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.primary500 // 🎨 파란색으로 변경
                ),
                shape = RoundedCornerShape(16.dp),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                LocalizedText(
                    resId = R.string.level_test_start_button,
                    style = AppTextStyles.cookieRunBold.copy(fontSize = if (isLandscape) 18.sp else 20.sp), // 🎨 폰트 크기 감소
                    color = AppColors.neutralWhite, // 🎨 흰색으로 변경
                    fontPolicy = FontPolicy.DEFAULT
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp)) // 🎨 스페이서 감소

            Button(
                onClick = onSkipTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp), // 🎨 버튼 높이 감소 (가로 모드 고려)
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.neutralWhite // 🎨 흰색으로 변경
                ),
                shape = RoundedCornerShape(16.dp),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                LocalizedText(
                    resId = R.string.level_test_skip_button,
                    style = AppTextStyles.cookieRunBold.copy(fontSize = if (isLandscape) 18.sp else 20.sp), // 🎨 폰트 크기 감소
                    color = AppColors.neutral700,
                    fontPolicy = FontPolicy.DEFAULT
                )
            }
        }
    }
}

/**
 * 레벨 테스트 문제 콘텐츠 - 화면 크기에 맞게 반응형으로 개선
 */
@Composable
private fun LevelTestQuestionContent(
    uiState: LevelTestUiState,
    onAction: (LevelTestAction) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

    // 🔧 화면 크기별 동적 조정
    val isSmallScreen = screenHeight < 700 // Pixel 5 등 작은 화면
    val isVerySmallScreen = screenHeight < 600 // 매우 작은 화면

    // 🔧 화면 크기에 따른 패딩 조정
    val horizontalPadding = when {
        isLandscape -> 32.dp
        screenWidth < 400 -> 16.dp
        else -> 24.dp
    }

    val verticalPadding = when {
        isVerySmallScreen -> 8.dp
        isSmallScreen -> 12.dp
        else -> 20.dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    ) {
        uiState.currentQuestion?.let { question ->
            // 🔧 전체 화면을 문제 영역과 선택지 영역으로 나누기
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 🎨 문제 영역 - 상단 고정 (전체 높이의 30-40% 사용)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f), // 전체 높이의 35% 사용
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.neutralWhite
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 문제 텍스트
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    if (isSmallScreen) 12.dp else 16.dp
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.neutral100
                            )
                        ) {
                            Text(
                                text = question.question,
                                fontSize = when {
                                    isVerySmallScreen -> 16.sp
                                    isSmallScreen -> 17.sp
                                    else -> 18.sp
                                },
                                fontWeight = FontWeight.Medium,
                                color = AppColors.neutral800,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        if (isSmallScreen) 14.dp else 18.dp
                                    )
                                    .wrapContentHeight(Alignment.CenterVertically)
                            )
                        }

                        // 🎨 Skip 버튼 - 위치 조정
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onAction(LevelTestAction.SkipTest) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.neutral200.copy(alpha = 0.9f)
                            )
                        ) {
                            LocalizedText(
                                resId = R.string.level_test_skip_button,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                color = AppColors.neutral600,
                                fontPolicy = FontPolicy.PRETENDA_ALL,
                                modifier = Modifier.padding(
                                    horizontal = if (isSmallScreen) 8.dp else 12.dp,
                                    vertical = if (isSmallScreen) 4.dp else 6.dp
                                )
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(
                        if (isSmallScreen) 8.dp else 12.dp
                    )
                )

                // 🎨 선택지 영역 - 하단 (전체 높이의 60-65% 사용)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f), // 전체 높이의 60% 사용
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.neutralWhite
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                if (isSmallScreen) 12.dp else 16.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 12.dp) // 🔧 선택지 간격 추가
                    ) {
                        question.options.forEachIndexed { index, option ->
                            val isSelected = uiState.selectedAnswerIndex == index
                            val isCorrect = question.isCorrectAnswer(index)
                            val showResult = uiState.showAnswerResult

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // 🔧 각 선택지가 균등한 높이 사용
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        if (!showResult) {
                                            onAction(LevelTestAction.SelectAnswer(index))
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        showResult && isCorrect -> AppColors.green600
                                        showResult && isSelected && !isCorrect -> AppColors.red600
                                        isSelected && !showResult -> AppColors.primary100 // 🎨 선택 시 연한 파란색
                                        else -> AppColors.neutralWhite // 🎨 기본 배경 흰색
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (isSelected) 4.dp else 2.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = when {
                                            isVerySmallScreen -> 14.sp
                                            isSmallScreen -> 15.sp
                                            else -> 16.sp
                                        },
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = when {
                                            showResult && (isCorrect || (isSelected && !isCorrect)) -> AppColors.neutralWhite
                                            isSelected && !showResult -> AppColors.primary500 // 🎨 선택 시 진한 파란색
                                            else -> AppColors.neutral800 // 🎨 기본 텍스트 색상
                                        },
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(
                                            horizontal = if (isSmallScreen) 8.dp else 12.dp,
                                            vertical = if (isSmallScreen) 4.dp else 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
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
    val contentWidth = when {
        screenWidth >= 800 -> 0.6f // Tablet
        isLandscape -> 0.8f // Phone landscape
        else -> 1f // Phone portrait
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AppColors.primary50, AppColors.primary100)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(contentWidth)
                .padding(
                    horizontal = if (isLandscape) 32.dp else 24.dp,
                    vertical = if (isLandscape) 16.dp else 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 🎨 완료 아이콘 → 로고 유지
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 32.dp))

            // 🎨 큰 제목 (완료)
            LocalizedText(
                resId = R.string.level_test_complete_title,
                style = AppTextStyles.cookieRunBlackRegular.copy(fontSize = if (isLandscape) 28.sp else 36.sp),
                color = AppColors.neutral800,
                fontPolicy = FontPolicy.DEFAULT,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

            // 🎨 작은 부제 (레벨 결과 안내)
            LocalizedText(
                resId = R.string.level_test_result_level,
                formatArgs = arrayOf(finalLevel),
                style = AppTextStyles.cookieRunRegular.copy(fontSize = if (isLandscape) 16.sp else 18.sp),
                color = AppColors.neutral600,
                fontPolicy = FontPolicy.DEFAULT,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 48.dp))

            // 🎨 Start Reading 버튼
            Button(
                onClick = onStartReading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.primary500
                ),
                shape = RoundedCornerShape(16.dp),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                LocalizedText(
                    resId = R.string.level_test_start_reading_button,
                    style = AppTextStyles.cookieRunBold.copy(fontSize = if (isLandscape) 18.sp else 20.sp),
                    color = AppColors.neutralWhite,
                    fontPolicy = FontPolicy.DEFAULT
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

            // 🎨 Retry 버튼
            Button(
                onClick = onRetakeTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.neutralWhite
                ),
                shape = RoundedCornerShape(16.dp),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                LocalizedText(
                    resId = R.string.level_test_retry_button,
                    style = AppTextStyles.cookieRunBold.copy(fontSize = if (isLandscape) 18.sp else 20.sp),
                    color = AppColors.neutral700,
                    fontPolicy = FontPolicy.DEFAULT
                )
            }
        }
    }
}
