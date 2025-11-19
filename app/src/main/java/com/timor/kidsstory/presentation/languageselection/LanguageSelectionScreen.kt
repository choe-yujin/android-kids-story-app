package com.timor.kidsstory.presentation.languageselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.languageselection.components.LanguageCard

/**
 * 언어 선택 화면 Root
 */
@Composable
fun LanguageSelectionScreenRoot(
    onLevelTestNavigation: (String) -> Unit,
    onBookshelfNavigation: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 네비게이션 처리
    LaunchedEffect(uiState.navigationTarget) {
        when (val target = uiState.navigationTarget) {
            is LanguageSelectionNavigationTarget.LevelTest -> {
                onLevelTestNavigation(target.language)
                viewModel.onNavigationCompleted()
            }
            is LanguageSelectionNavigationTarget.Bookshelf -> {
                onBookshelfNavigation(target.language, target.level)
                viewModel.onNavigationCompleted()
            }
            null -> {
                // 아직 네비게이션 대상이 결정되지 않음
            }
        }
    }
    
    LanguageSelectionScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

/**
 * 언어 선택 화면 - 태블릿 국기 가운데 정렬 개선
 */
@Composable
fun LanguageSelectionScreen(
    uiState: LanguageSelectionUiState,
    onAction: (LanguageSelectionAction) -> Unit,
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
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isLandscape) 64.dp else 32.dp,
                        vertical = if (isLandscape) 32.dp else 64.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // 언어 선택 카드들 - 🆕 태블릿에서 가운데 정렬 개선
                if (isLandscape) {
                    // 가로 모드: 한 줄로 배치 - 가운데 정렬 추가
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally), // 🆕 가운데 정렬 추가
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.availableLanguages) { language ->
                            LanguageCard(
                                language = language,
                                isSelected = uiState.selectedLanguage?.code == language.code,
                                onClick = {
                                    onAction(LanguageSelectionAction.OnLanguageSelected(language.code))
                                }
                            )
                        }
                    }
                } else {
                    // 세로 모드: 격자 배치 - 기존 유지
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 첫 번째 줄: 영어, 한국어
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            uiState.availableLanguages.take(2).forEach { language ->
                                LanguageCard(
                                    language = language,
                                    isSelected = uiState.selectedLanguage?.code == language.code,
                                    onClick = {
                                        onAction(LanguageSelectionAction.OnLanguageSelected(language.code))
                                    }
                                )
                            }
                        }
                        
                        // 두 번째 줄: 테툼어
                        if (uiState.availableLanguages.size > 2) {
                            Row {
                                uiState.availableLanguages.drop(2).take(1).forEach { language ->
                                    LanguageCard(
                                        language = language,
                                        isSelected = uiState.selectedLanguage?.code == language.code,
                                        onClick = {
                                            onAction(LanguageSelectionAction.OnLanguageSelected(language.code))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 에러 메시지 표시
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
    }
}
