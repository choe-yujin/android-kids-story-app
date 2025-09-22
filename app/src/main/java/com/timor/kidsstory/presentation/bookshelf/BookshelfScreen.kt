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
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import com.timor.kidsstory.presentation.bookshelf.components.ManagementTabBar
import com.timor.kidsstory.presentation.bookshelf.components.ProgressAndAttendanceSection
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusBar
import com.timor.kidsstory.presentation.bookshelf.components.SelectableBookCover
import com.timor.kidsstory.presentation.bookshelf.components.SelectionConfirmationPopup
import com.timor.kidsstory.presentation.bookshelf.components.SelectionFloatingActionButton
import com.timor.kidsstory.presentation.bookshelf.components.ActionType
import com.timor.kidsstory.presentation.bookshelf.components.AppUpdateDialog
import com.timor.kidsstory.presentation.bookshelf.components.ReadingStatusFilter
import com.timor.kidsstory.presentation.bookshelf.components.filter.FilterBar
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarState
import com.timor.kidsstory.presentation.progress.ProgressViewModel
import com.timor.kidsstory.ui.theme.AppColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import androidx.compose.material3.MaterialTheme
import com.timor.kidsstory.ui.components.FontPolicy

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

    LaunchedEffect(Unit) {
        bookshelfViewModel.onAction(BookShelfAction.CheckForUpdate)
    }

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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.primary50)
    ) {
        if (bookshelfState.showUpdateDialog && bookshelfState.appVersionInfo != null) {
            Log.d("BookshelfScreen", "Showing AppUpdateDialog")
            AppUpdateDialog(
                versionInfo = bookshelfState.appVersionInfo,
                onDismiss = { onBookshelfAction(BookShelfAction.ShowUpdateDialog(false)) },
                onPostpone = { onBookshelfAction(BookShelfAction.PostponeUpdate) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // 헤더 - 전체 가로 공간 차지
            BookshelfHeader(
                currentLanguage = bookshelfState.currentLanguage,
                isManagementMode = bookshelfState.isManagementMode, // 🆕 관리 모드 상태
                downloadableItemsCount = bookshelfState.downloadableItemsCount, // 🆕 다운로드 가능 항목 수
                hasCheckedUpdates = bookshelfState.hasCheckedUpdates, // 🆕 업데이트 확인 여부
                onSettingClick = {
                    onBookshelfAction(BookShelfAction.SettingClick)
                },
                onLanguageClick = {
                    onBookshelfAction(BookShelfAction.ShowLanguageDialog(true))
                },
                onChatbotClick = {
                    onBookshelfAction(BookShelfAction.ChatbotClick)
                },
                onManagementModeToggle = { // 🆕 관리 모드 전환
                    onBookshelfAction(BookShelfAction.ToggleManagementMode)
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
                    // 🆕 필터 바 또는 관리 탭 바 (상호 전환)
                    if (bookshelfState.isManagementMode) {
                        // 관리 모드: ManagementTabBar
                        ManagementTabBar(
                            selectedTab = bookshelfState.selectedManagementTab,
                            updateCount = bookshelfState.updatableBooks.size,
                            onTabSelected = { tab ->
                                onBookshelfAction(BookShelfAction.SelectManagementTab(tab))
                            }
                        )
                    } else {
                        // 일반 모드: FilterBar
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
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 책 그리드 또는 빈 상태
                    if (bookshelfState.filteredBooks.isEmpty()) {
                        EmptyBookshelf(
                            isFiltered = bookshelfState.filterBarState.selectedFilter != FilterBarCategory.All,
                            isLoading = bookshelfState.isLoading
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = if (isTablet) GridCells.Fixed(4) else GridCells.Fixed(3), // 태블릿은 4열, 폰은 3열
                            horizontalArrangement = if (isTablet) Arrangement.spacedBy(48.dp) else Arrangement.spacedBy(8.dp), // 폰 간격 줄임
                            verticalArrangement = if (isTablet) Arrangement.spacedBy(64.dp) else Arrangement.spacedBy(12.dp), // 폰 간격 줄임
                            contentPadding = if (isTablet) PaddingValues(
                                start = 80.dp,
                                end = 40.dp,
                                top = 24.dp,
                                bottom = 40.dp
                            ) else PaddingValues(
                                start = 16.dp, // 폰 패딩 조정
                                end = 16.dp, // 폰 패딩 조정
                                top = 12.dp, // 폰 패딩 조정
                                bottom = 12.dp // 폰 패딩 조정
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(bookshelfState.filteredBooks) { book ->
                                // 🆕 관리 모드에 따른 BookCover 전환
                                if (bookshelfState.isManagementMode) {
                                    // 관리 모드: SelectableBookCover (체크박스 지원)
                                    SelectableBookCover(
                                        book = book,
                                        isSelected = book.storyId in bookshelfState.selectedBookIds,
                                        onSelectionChanged = { isSelected ->
                                            onBookshelfAction(
                                                BookShelfAction.ToggleBookSelection(book.storyId, isSelected)
                                            )
                                        },
                                        onBookClick = {
                                            // 관리 모드에서는 책 읽기 비활성화 (체크박스 우선)
                                        }
                                    )
                                } else {
                                    // 일반 모드: 기존 BookCover
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
                }
                
                // 🆕 오른쪽 세로 읽음 상태 바 (관리 모드에서는 숨김)
                if (!bookshelfState.isManagementMode) {
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
        }

        // 🆕 플로팅 액션 버튼 (관리 모드에서 선택된 항목이 있을 때)
        if (bookshelfState.isManagementMode && bookshelfState.selectedBookIds.isNotEmpty()) {
            val actionType = when (bookshelfState.selectedManagementTab) {
                com.timor.kidsstory.presentation.bookshelf.model.ManagementTab.DOWNLOAD -> ActionType.DOWNLOAD
                com.timor.kidsstory.presentation.bookshelf.model.ManagementTab.UPDATE -> ActionType.UPDATE
                else -> ActionType.DOWNLOAD // ALL 탭에서는 기본적으로 다운로드
            }
            
            SelectionFloatingActionButton(
                selectedBooks = bookshelfState.filteredBooks.filter { it.storyId in bookshelfState.selectedBookIds },
                actionType = actionType,
                onClick = {
                    onBookshelfAction(BookShelfAction.ExecuteSelectedActions)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }

        // 🆕 선택 확인 팝업
        if (bookshelfState.showConfirmationPopup) {
            val selectedBooks = bookshelfState.filteredBooks.filter { 
                it.storyId in bookshelfState.selectedBookIds 
            }
            val actionType = when (bookshelfState.selectedManagementTab) {
                com.timor.kidsstory.presentation.bookshelf.model.ManagementTab.DOWNLOAD -> ActionType.DOWNLOAD
                com.timor.kidsstory.presentation.bookshelf.model.ManagementTab.UPDATE -> ActionType.UPDATE
                else -> ActionType.DOWNLOAD
            }
            
            SelectionConfirmationPopup(
                selectedBooks = selectedBooks,
                actionType = actionType,
                onConfirm = {
                    onBookshelfAction(BookShelfAction.ShowConfirmationPopup(false))
                },
                onCancel = {
                    onBookshelfAction(BookShelfAction.ShowConfirmationPopup(false))
                }
            )
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
        if (shouldShowAttendancePopup) {
            Log.d("BookshelfScreen", "Showing AttendancePopup. shouldShowAttendancePopup: $shouldShowAttendancePopup")
            AttendancePopup(
                isVisible = shouldShowAttendancePopup,
                streakCount = attendanceStreak,
                onDismiss = {
                    onBookshelfAction(BookShelfAction.DismissAttendancePopup)
                }
            )
        }
        // 레벨 테스트 결과 팝업
        if (bookshelfState.showLevelResultPopup && bookshelfState.levelResultPopupMessage != null) {
            Log.d("BookshelfScreen", "Showing LevelTestResultPopup. showLevelResultPopup: ${bookshelfState.showLevelResultPopup}, message: ${bookshelfState.levelResultPopupMessage}")
            AlertDialog(
                onDismissRequest = { onBookshelfAction(BookShelfAction.DismissLevelResultPopup) },
                title = { LocalizedText(resId = R.string.level_test_reading_adventure_title, style = MaterialTheme.typography.titleLarge) },
                text = {
                    bookshelfState.levelResultPopupMessage?.let { localizedMessage ->
                        LocalizedText(
                            resId = localizedMessage.resId,
                            formatArgs = localizedMessage.formatArgs,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { onBookshelfAction(BookShelfAction.DismissLevelResultPopup) }) {
                        LocalizedText(resId = R.string.attendance_popup_button_ok, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )
        }
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
