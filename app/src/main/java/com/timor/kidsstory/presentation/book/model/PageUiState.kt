package com.timor.kidsstory.presentation.book.model

// 단일 페이지의 상태
data class PageUiState(
    val imageUrl: String,
    val texts: List<String>,
    val pageNumber: Int,   // 1부터 시작하는 페이지 번호 (화면에 표시)
    val totalPages: Int    // 전체 페이지 수
) {
    // "1/14" ~ "14/14"
    val pageDisplay: String get() = "$pageNumber/$totalPages"
}