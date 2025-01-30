package com.timor.kidsstory.presentation.reader.model

// 읽기 화면 관련 UI 상태 모델
data class PageUiState(
    val imageUrl: String,
    val texts: List<String>,
    val pageNumber: Int,
    val totalPages: Int
) {
    val pageDisplay: String get() = "$pageNumber/$totalPages"
}