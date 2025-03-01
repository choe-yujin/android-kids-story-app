package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.components.BookshelfHeader
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState

// 책장 화면 UI 컴포넌트
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    onBookSelected: (Int) -> Unit,
    onMakerClick: () -> Unit,
    onLanguageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E0)) // 전체 배경색 적용
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // 헤더 추가
            BookshelfHeader(
                currentLanguage = state.currentLanguage,
                onMakerClick = onMakerClick,
                onLanguageClick = onLanguageClick
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
                        onClick = { onBookSelected(state.books.indexOf(bookState)) }
                    )
                }
            }
        }
    }
}