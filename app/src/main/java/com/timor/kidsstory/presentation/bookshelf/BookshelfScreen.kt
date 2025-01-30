package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.bookshelf.components.BookCover
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState

// 책장 화면 UI 컴포넌트
@Composable
fun BookshelfScreen(
    state: BookshelfUiState,
    onBookSelected: (Int) -> Unit
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
}