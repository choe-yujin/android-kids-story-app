package com.timor.kidsstory.presentation.reader.model

// 읽기 화면 관련 UI 상태 모델
data class ReaderUiState(
    val currentPage: Int = 0, // 0-based index for internal use
    val pages: List<PageUiState> = emptyList()
)