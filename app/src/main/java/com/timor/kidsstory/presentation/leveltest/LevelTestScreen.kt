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

/**
 * 레벨 테스트 화면 Root
 */
@Composable
fun LevelTestScreenRoot(
    language: String,
    onNavigateToBookshelf: (language: String, level: Int) -> Unit,
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
                onNavigateToBookshelf(target.language, target.level)
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
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
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
                    isLandscape = isLandscape
                )
            }
            is TestState.InProgress -> {
                LevelTestQuestionContent(
                    uiState = uiState,
                    onAction = onAction,
                    isLandscape = isLandscape
                )
            }
            is TestState.Completed -> {
                LevelTestCompletedContent(
                    finalLevel = uiState.finalLevel ?: 3,
                    language = uiState.language,
                    onStartReading = { onAction(LevelTestAction.StartReading) },
                    onRetakeTest = { onAction(LevelTestAction.RestartTest) },
                    isLandscape = isLandscape
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
    isLandscape: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = if (isLandscape) 64.dp else 24.dp,
                vertical = if (isLandscape) 24.dp else 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "레벨 테스트",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "읽기 수준에 맞는 책을 추천해드립니다",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
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
            Text(
                text = "테스트 시작",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
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
            Text(
                text = "건너뛰기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
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
    isLandscape: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = if (isLandscape) 64.dp else 24.dp,
                vertical = if (isLandscape) 24.dp else 32.dp
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 헤더 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "레벨 ${uiState.currentLevel}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            OutlinedButton(
                onClick = { onAction(LevelTestAction.SkipTest) },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("건너뛰기")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 문제 영역
        uiState.currentQuestion?.let { question ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = question.question,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 선택지들
                    question.options.forEachIndexed { index, option ->
                        val isSelected = uiState.selectedAnswerIndex == index
                        val isCorrect = question.isCorrectAnswer(index)
                        val showResult = uiState.showAnswerResult
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = { 
                                if (!showResult) {
                                    onAction(LevelTestAction.SelectAnswer(index))
                                }
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    showResult && isCorrect -> Color(0xFF4CAF50) // 정답: 초록색
                                    showResult && isSelected && !isCorrect -> Color(0xFFF44336) // 선택한 오답: 빨간색
                                    isSelected && !showResult -> MaterialTheme.colorScheme.primaryContainer // 선택됨
                                    else -> MaterialTheme.colorScheme.surface // 기본
                                }
                            )
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp,
                                color = when {
                                    showResult && (isCorrect || (isSelected && !isCorrect)) -> Color.White
                                    isSelected && !showResult -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
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
    isLandscape: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = if (isLandscape) 64.dp else 24.dp,
                vertical = if (isLandscape) 24.dp else 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "테스트 완료!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "당신의 레벨은 $finalLevel 입니다",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
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
            Text(
                text = "책 읽기 시작하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
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
            Text(
                text = "다시 테스트하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
