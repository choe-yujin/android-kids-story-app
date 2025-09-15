package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.presentation.attendance.AttendancePopup
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.components.BookshelfHeader
import com.timor.kidsstory.presentation.bookshelf.components.EmptyBookshelf
import com.timor.kidsstory.presentation.bookshelf.components.LanguageDialog
import com.timor.kidsstory.presentation.bookshelf.components.ProgressAndAttendanceSection
import com.timor.kidsstory.presentation.bookshelf.components.filter.FilterBar
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 책장 화면 UI 컴포넌트
 * - 사용자에게 전체 동화책 목록을 그리드 형태로 표시
 * - 언어 선택, 설정, 챗봇 기능 접근 제공
 * - 출석 체크 및 읽기 진도 표시
 * - 생명주기에 따른 배경음악 관리
 *
 * @param state 책장 화면 UI 상태
 * @param lifecycleOwner 생명주기 소유자 (기본값: 현재 콤포저블의 생명주기)
 * @param onAction 사용자 액션 처리 콜백
 * @param viewModel 뷰모델 (출석 및 진도 데이터 접근용)
 */
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onAction: (BookShelfAction) -> Unit,
    viewModel: BookshelfViewModel = hiltViewModel()
) {
    // 출석 및 진도 관련 상태 수집
    val attendanceStreak by viewModel.attendanceStreak.collectAsState()
    val shouldShowAttendancePopup by viewModel.shouldShowAttendancePopup.collectAsState()
    val readingProgress by viewModel.readingProgress.collectAsState()
    val completedBooksCount by viewModel.completedBooksCount.collectAsState()
    val totalBooksCount by viewModel.totalBooksCount.collectAsState()

    // 음악 상태 변경 감지 및 처리
    LaunchedEffect(state.isMusicOn) {
        if (state.isMusicOn) {
            onAction(BookShelfAction.StartMusic)
        } else {
            onAction(BookShelfAction.StopMusic)
        }
    }

    // 앱 생명주기에 따른 음악 제어
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> onAction(BookShelfAction.StopMusic)// 앱이 백그라운드로 가면 정지
                Lifecycle.Event.ON_START -> if (state.isMusicOn) onAction(BookShelfAction.StartMusic) // 다시 돌아오면 실행
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 메인 UI 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.primary50) // 전체 배경색 적용
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // 헤더 - 언어 선택, 설정, 챗봇 버튼과 출석/진도 표시 포함
            BookshelfHeader(
                currentLanguage = state.currentLanguage,
                onSettingClick = {
                    onAction(BookShelfAction.SettingClick)
                },
                onLanguageClick = {
                    onAction(BookShelfAction.ShowLanguageDialog(true))
                },
                onChatbotClick = {
                    onAction(BookShelfAction.ChatbotClick)
                },
                // 출석 및 진도 컴포넌트 추가
                progressAndAttendanceContent = {
                    ProgressAndAttendanceSection(
                        streakCount = attendanceStreak,
                        readingProgress = readingProgress,
                        completedBooks = completedBooksCount,
                        totalBooks = totalBooksCount,
                        onMyPageClick = {
                            onAction(BookShelfAction.MyPageClick)
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 선택된 언어 코드를 FilterBar에 전달
            FilterBar(
                filterBarState = state.filterBarState,
                selectedLanguageCode = state.currentLanguage.code, // 현재 선택된 언어 코드 전달
                onAllClick = {
                    onAction(BookShelfAction.SelectFilter(FilterBarCategory.All))
                },
                onStageClick = {
                    onAction(BookShelfAction.SelectFilter(FilterBarCategory.STAGE))
                },
                onCategoryClick = {
                    onAction(BookShelfAction.SelectFilter(FilterBarCategory.CATEGORY))
                },
                onLevelClick = { level ->
                    onAction(BookShelfAction.SelectFilter(stage = level))
                },
                onBookCategoryClick = { bookCategory ->
                    onAction(BookShelfAction.SelectFilter(category = bookCategory))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 책 그리드 또는 빈 상태 표시
            if (state.filteredBooks.isEmpty()) {
                // 책이 없을 때 빈 상태 표시 (선택된 언어로 표시)
                EmptyBookshelf(
                    isFiltered = state.filterBarState.selectedFilter != FilterBarCategory.All,
                    isLoading = state.isLoading,
                    selectedLanguageCode = state.currentLanguage.code // 언어 코드 전달
                )
            } else {
                // 책 그리드 표시
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),  // 5열 그리드
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp), // 상단 4dp, 하단 24dp 패딩
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp)
                ) {
                    items(state.filteredBooks) { book ->
                        BookCover(
                            book = book,
                            onClick = { onAction(BookShelfAction.BookSelect(state.books.indexOf(book))) },
                            onDownloadClick = if (!book.isDownloaded) {
                                { onAction(BookShelfAction.DownloadBook(state.books.indexOf(book))) }
                            } else null
                        )
                    }
                }
            }
        }

        // 언어 선택 다이얼로그 표시
        if (state.showLanguageDialog) {
            LanguageDialog(
                languages = LanguageConstants.SUPPORTED_LANGUAGES,
                selectedLanguage = state.currentLanguage,
                onLanguageSelected = {
                    onAction(BookShelfAction.ChangeLanguage(it))
                    // 액티비티 재시작 제거 - LocalizedText가 동적으로 처리함
                },
                onDismiss = {
                    onAction(BookShelfAction.ShowLanguageDialog(false))
                }
            )
        }

        // 출석 축하 팝업 표시
        AttendancePopup(
            isVisible = shouldShowAttendancePopup,
            streakCount = attendanceStreak,
            onDismiss = {
                onAction(BookShelfAction.DismissAttendancePopup)
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 300)
@Composable
private fun BookShelfScreenPreview() {
    KidsStoryTheme {
        BookshelfScreen(
            state = BookshelfUiState(),
            onAction = {}
        )
    }
}
