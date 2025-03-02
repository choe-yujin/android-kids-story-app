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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.components.BookshelfHeader
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 책장 화면 UI 컴포넌트
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    onBookSelected: (Int) -> Unit,
    onMakerClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onChatbotClick: () -> Unit
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
                onLanguageClick = onLanguageClick,
                onChatbotClick = onChatbotClick,
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

@Preview(
    showBackground = true,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun BookshelfScreenPreview() {
    KidsStoryTheme {
        BookshelfScreen(
            state = BookshelfUiState(
                books = List(10) { index ->  // 책 10개로 테스트
                    BookCoverUiState(
                        imageUrl = "",  // 이미지 없는 테스트 데이터 회색 박스 출력
                        title = "Sample Book $index",
                        storyId = "preview_sample_$index"
                    )
                },
                currentLanguage = LanguageConstants.TETUM
            ),
            onBookSelected = {},
            onMakerClick = {},
            onLanguageClick = {},
            onChatbotClick = {}
        )
    }
}
