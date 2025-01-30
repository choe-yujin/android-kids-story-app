package com.timor.kidsstory.presentation.bookshelf.model

// 책장 화면 UI 상태
data class BookshelfUiState(
    val books: List<BookCoverUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)