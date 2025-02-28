package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 책장 화면 UI 컴포넌트
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    onBookSelected: (Int) -> Unit,
    onChatbotClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.books) { bookState ->
                BookCover(
                    state = bookState,
                    onClick = { onBookSelected(state.books.indexOf(bookState)) }
                )
            }
        }
    }

    // TODO: 챗봇 임시 버튼 생성을 위한 박스 생성 - 추후 메뉴가 생기면 삭제 필요
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = {
                onChatbotClick()
            },
        ) {
            Text("챗봇 화면으로")
        }
    }
}

@Preview(showBackground = true, widthDp = 600, heightDp = 300)
@Composable
private fun BookshelfScreenPreview() {
    KidsStoryTheme {
        BookshelfScreen(
            state = BookshelfUiState(),
            onBookSelected = {},
            onChatbotClick = {}
        )
    }
}