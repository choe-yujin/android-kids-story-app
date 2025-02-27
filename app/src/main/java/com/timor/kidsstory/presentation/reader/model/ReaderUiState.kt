package com.timor.kidsstory.presentation.reader.model

// 읽기 화면 관련 UI 상태 모델
data class ReaderUiState(
    val currentPage: Int = 0,
    val pages: List<PageUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)