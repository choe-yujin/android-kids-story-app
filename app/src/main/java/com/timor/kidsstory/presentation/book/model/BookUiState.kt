package com.timor.kidsstory.presentation.book.model

// 책 읽기 화면의 전체 상태
data class BookUiState(
    val currentPageIndex: Int = 0, // 0부터 시작하는 현재 페이지 인덱스
    val pages: List<PageUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)