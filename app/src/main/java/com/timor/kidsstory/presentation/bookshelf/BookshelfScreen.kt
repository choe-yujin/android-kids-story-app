package com.timor.kidsstory.presentation.bookshelf

import android.app.Activity
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.LocaleHelper.updateLanguage
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.components.BookshelfHeader
import com.timor.kidsstory.presentation.bookshelf.components.LanguageDialog
import com.timor.kidsstory.presentation.bookshelf.components.filter.FilterBar
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.presentation.bookshelf.model.FilterBarCategory
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 책장 화면 UI 컴포넌트
 * - 사용자에게 전체 동화책 목록을 그리드 형태로 표시
 * - 언어 선택, 설정, 챗봇 기능 접근 제공
 * - 생명주기에 따른 배경음악 관리
 *
 * @param state 책장 화면 UI 상태
 * @param lifecycleOwner 생명주기 소유자 (기본값: 현재 콤포저블의 생명주기)
 * @param onAction 사용자 액션 처리 콜백
 */
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onAction: (BookShelfAction) -> Unit,
) {
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
            // 헤더 추가 - 언어 선택, 설정, 챗봇 버튼 포함
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
            )

            Spacer(modifier = Modifier.height(6.dp))

            FilterBar(
                filterBarState = state.filterBarState,
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

            // 책 그리드 표시
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),  // 5열 그리드
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
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

        // 언어 선택 다이얼로그 표시
        if (state.showLanguageDialog) {
            val context = LocalContext.current

            LanguageDialog(
                languages = LanguageConstants.SUPPORTED_LANGUAGES,
                selectedLanguage = state.currentLanguage,
                onLanguageSelected = {
                    onAction(BookShelfAction.ChangeLanguage(it))

                    // 언어 변경
                    context.updateLanguage(it.code)

                    // 화면 재시작으로 동기화
                    val activity = context as? Activity
                    activity?.recreate()
                },
                onDismiss = {
                    onAction(BookShelfAction.ShowLanguageDialog(false))
                }
            )
        }
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