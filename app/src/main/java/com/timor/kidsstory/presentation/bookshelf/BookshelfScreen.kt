package com.timor.kidsstory.presentation.bookshelf

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.presentation.attendance.AttendancePopup
import com.timor.kidsstory.presentation.attendance.AttendanceViewModel
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.components.BookshelfHeader
import com.timor.kidsstory.presentation.bookshelf.components.EmptyBookshelf
import com.timor.kidsstory.presentation.bookshelf.components.LanguageDialog
import com.timor.kidsstory.presentation.bookshelf.components.ProgressAndAttendanceSection
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusBar
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.presentation.bookshelf.components.filter.FilterBar
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.presentation.progress.ProgressViewModel
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 책장 화면 UI 컴포넌트 (리팩토링됨)
 * 
 * Clean Architecture 적용:
 * - UI는 완전히 stateless
 * - 3개의 분리된 ViewModel 사용
 * - 각 ViewModel의 상태를 조합하여 UI 구성
 * 
 * @param bookshelfViewModel 책장 상태 관리 (책 목록, 언어, 음악)
 * @param attendanceViewModel 출석 상태 관리 (출석 팝업, 연속 출석)
 * @param progressViewModel 읽기 진도 상태 관리 (완독 책, 진도율)
 * @param lifecycleOwner 생명주기 소유자
 * @param onBookSelect 책 선택 콜백
 * @param onSettingClick 설정 버튼 클릭 콜백
 * @param onChatbotClick 챗봇 버튼 클릭 콜백
 */
@Composable
fun BookshelfScreen(
    bookshelfViewModel: BookshelfViewModel = hiltViewModel(),
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    progressViewModel: ProgressViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBookSelect: (Int) -> Unit = {},
    onSettingClick: () -> Unit = {},
    onChatbotClick: () -> Unit = {},
    onMyPageClick: () -> Unit = {}
) {
    // 각 ViewModel의 상태 수집 (Stateless UI)
    val bookshelfState by bookshelfViewModel.state.collectAsState()
    val attendanceState by attendanceViewModel.attendanceState.collectAsState()
    val shouldShowAttendancePopup by attendanceViewModel.shouldShowAttendancePopup.collectAsState()
    val progressState by progressViewModel.progressState.collectAsState()

    /**
     * 언어 변경 시 진도 데이터 다시 로드
     */
    LaunchedEffect(bookshelfState.currentLanguage.code) {
        // 언어가 변경될 때마다 진도 로드
        Log.d("BookshelfScreen", "Language changed to: ${bookshelfState.currentLanguage.code}")
        progressViewModel.loadProgress(
            languageCode = bookshelfState.currentLanguage.code,
            totalBooks = 0 // 첫 로드시에는 0으로 전달
        )
    }
    
    // 책 목록이 로드된 후 총 책 수를 업데이트
    LaunchedEffect(bookshelfState.books.size) {
        if (bookshelfState.books.isNotEmpty()) {
            Log.d("BookshelfScreen", "Books loaded: ${bookshelfState.books.size} for ${bookshelfState.currentLanguage.code}")
            progressViewModel.loadProgress(
                languageCode = bookshelfState.currentLanguage.code,
                totalBooks = bookshelfState.books.size
            )
        }
    }

    // 음악 상태 변경 감지 및 처리
    LaunchedEffect(bookshelfState.isMusicOn) {
        if (bookshelfState.isMusicOn) {
            bookshelfViewModel.onAction(BookShelfAction.StartMusic)
        } else {
            bookshelfViewModel.onAction(BookShelfAction.StopMusic)
        }
    }

    // 앱 생명주기에 따른 음악 제어
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> bookshelfViewModel.onAction(BookShelfAction.StopMusic)
                Lifecycle.Event.ON_START -> {
                    if (bookshelfState.isMusicOn) {
                        bookshelfViewModel.onAction(BookShelfAction.StartMusic)
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 메인 UI 구성 (완전히 stateless)
    BookshelfScreenContent(
        bookshelfState = bookshelfState,
        attendanceStreak = attendanceState.currentStreak,
        shouldShowAttendancePopup = shouldShowAttendancePopup,
        readingProgress = progressState.progressPercentage,
        completedBooksCount = progressState.completedBooks,
        totalBooksCount = progressState.totalBooks,
        onBookshelfAction = { action ->
            when (action) {
                is BookShelfAction.BookSelect -> onBookSelect(action.index)
                is BookShelfAction.SettingClick -> onSettingClick()
                is BookShelfAction.ChatbotClick -> onChatbotClick()
                is BookShelfAction.MyPageClick -> onMyPageClick()
                is BookShelfAction.DismissAttendancePopup -> {
                    attendanceViewModel.onAttendancePopupDismiss()
                }
                else -> {
                    bookshelfViewModel.onAction(action)
                }
            }
        }
    )
}

/**
 * 책장 화면의 실제 UI 컨텐츠 (Stateless)
 * 
 * 모든 상태를 매개변수로 받아 완전히 stateless하게 구성
 */
@Composable
private fun BookshelfScreenContent(
    bookshelfState: BookshelfUiState,
    attendanceStreak: Int,
    shouldShowAttendancePopup: Boolean,
    readingProgress: Float,
    completedBooksCount: Int,
    totalBooksCount: Int,
    onBookshelfAction: (BookShelfAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.primary50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // 헤더 - 전체 가로 공간 차지
            BookshelfHeader(
                currentLanguage = bookshelfState.currentLanguage,
                onSettingClick = {
                    onBookshelfAction(BookShelfAction.SettingClick)
                },
                onLanguageClick = {
                    onBookshelfAction(BookShelfAction.ShowLanguageDialog(true))
                },
                onChatbotClick = {
                    onBookshelfAction(BookShelfAction.ChatbotClick)
                },
                // 출석 및 진도 컴포넌트 (stateless + 언어별 폰트)
                progressAndAttendanceContent = {
                    ProgressAndAttendanceSection(
                        streakCount = attendanceStreak,
                        readingProgress = readingProgress,
                        completedBooks = completedBooksCount,
                        totalBooks = totalBooksCount,
                        currentLanguage = bookshelfState.currentLanguage, // 현재 언어 전달
                        onMyPageClick = {
                            onBookshelfAction(BookShelfAction.MyPageClick)
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 필터바와 책 그리드 영역 - Reading Status Bar와 나란히 배치
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // 왼쪽 메인 컨텐츠 영역 (가로 영역을 더 넓게)
                Column(
                    modifier = Modifier.weight(1f) // weight를 늘려서 더 넓은 공간 확보
                ) {
                    // 필터 바
                    FilterBar(
                        filterBarState = bookshelfState.filterBarState,
                        currentLanguageCode = bookshelfState.currentLanguage.code,
                        onAllClick = {
                            onBookshelfAction(BookShelfAction.SelectFilter(FilterBarCategory.All))
                        },
                        onStageClick = {
                            onBookshelfAction(BookShelfAction.SelectFilter(FilterBarCategory.STAGE))
                        },
                        onCategoryClick = {
                            onBookshelfAction(BookShelfAction.SelectFilter(FilterBarCategory.CATEGORY))
                        },
                        onLevelClick = { level ->
                            onBookshelfAction(BookShelfAction.SelectFilter(stage = level))
                        },
                        onBookCategoryClick = { bookCategory ->
                            onBookshelfAction(BookShelfAction.SelectFilter(category = bookCategory))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 책 그리드 또는 빈 상태
                    if (bookshelfState.filteredBooks.isEmpty()) {
                        EmptyBookshelf(
                            isFiltered = bookshelfState.filterBarState.selectedFilter != FilterBarCategory.All,
                            isLoading = bookshelfState.isLoading,
                            currentLanguageCode = bookshelfState.currentLanguage.code // 언어 코드 전달
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            contentPadding = PaddingValues(
                                start = 48.dp,
                                end = 16.dp, // 오른쪽 패딩 축소 ReadingStatusBar 공간 확보
                                top = 4.dp,
                                bottom = 24.dp
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(bookshelfState.filteredBooks) { book ->
                                BookCover(
                                    book = book,
                                    onClick = { 
                                        onBookshelfAction(
                                            BookShelfAction.BookSelect(bookshelfState.books.indexOf(book))
                                        ) 
                                    },
                                    onDownloadClick = if (!book.isDownloaded) {
                                        { 
                                            onBookshelfAction(
                                                BookShelfAction.DownloadBook(bookshelfState.books.indexOf(book))
                                            ) 
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }
                
                // 오른쪽 세로 읽음 상태 바 (세로 가운데 정렬)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp)
                        .width(72.dp), // 고정 너비로 공간 제한
                    contentAlignment = Alignment.Center // TopCenter에서 Center로 변경
                ) {
                    ReadingStatusBar(
                        currentLanguageCode = bookshelfState.currentLanguage.code,
                        hasBooks = bookshelfState.books.isNotEmpty(), // 전체 책 수로 변경
                        selectedStatus = bookshelfState.selectedReadingStatus ?: ReadingStatusFilter.ALL,
                        onStatusSelected = { status ->
                            onBookshelfAction(BookShelfAction.SelectReadingStatus(status))
                        }
                    )
                }
            }
        }

        // 언어 선택 다이얼로그
        if (bookshelfState.showLanguageDialog) {
            LanguageDialog(
                languages = LanguageConstants.SUPPORTED_LANGUAGES,
                selectedLanguage = bookshelfState.currentLanguage,
                onLanguageSelected = {
                    onBookshelfAction(BookShelfAction.ChangeLanguage(it))
                },
                onDismiss = {
                    onBookshelfAction(BookShelfAction.ShowLanguageDialog(false))
                }
            )
        }

        // 출석 축하 팝업
        AttendancePopup(
            isVisible = shouldShowAttendancePopup,
            streakCount = attendanceStreak,
            onDismiss = {
                onBookshelfAction(BookShelfAction.DismissAttendancePopup)
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 300)
@Composable
private fun BookShelfScreenPreview() {
    KidsStoryTheme {
        BookshelfScreenContent(
            bookshelfState = BookshelfUiState(),
            attendanceStreak = 5,
            shouldShowAttendancePopup = false,
            readingProgress = 0.3f,
            completedBooksCount = 3,
            totalBooksCount = 10,
            onBookshelfAction = {}
        )
    }
}
