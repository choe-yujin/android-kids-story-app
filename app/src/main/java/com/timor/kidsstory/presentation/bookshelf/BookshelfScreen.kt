package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.components.BookshelfHeader
import com.timor.kidsstory.presentation.bookshelf.components.LanguageDialog
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 책장 화면 UI 컴포넌트
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onAction: (BookShelfAction) -> Unit,
) {
    LaunchedEffect(state.isMusicOn) {
        if (state.isMusicOn) {
            onAction(BookShelfAction.StartMusic)
        } else {
            onAction(BookShelfAction.StopMusic)
        }
    }

    // 백그라운드로 갔을경우 음악 정지
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
            // 헤더 추가
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp)
            ) {
                items(state.books) { bookState ->
                    BookCover(
                        state = bookState,
                        onClick = { onAction(BookShelfAction.BookSelect(state.books.indexOf(bookState))) }
                    )
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

