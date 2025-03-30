package com.timor.kidsstory.presentation.bookshelf.model

/**
 * 책 표지 UI 상태 클래스
 * - 책장 화면에서 표시되는 개별 책 표지의 상태 정보
 *
 * @property imageUrl 책 표지 이미지 URL
 * @property title 책 제목
 * @property storyId 책 고유 ID (예: 801_en-ph)
 */
data class BookCoverUiState(
    val imageUrl: String,
    val title: String,
    val storyId: String
)