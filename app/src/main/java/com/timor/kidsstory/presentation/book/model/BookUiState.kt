package com.timor.kidsstory.presentation.book.model

/**
 * 책 읽기 화면의 전체 상태 클래스
 * - 책 읽기 화면의 모든 상태 정보를 담는 불변 데이터 클래스
 *
 * @property currentPageIndex 현재 보고 있는 페이지 인덱스 (0부터 시작)
 * @property pages 책의 모든 페이지 정보
 * @property isLoading 로딩 중 상태
 * @property error 오류 메시지 (있을 경우)
 */
data class BookUiState(
    val currentPageIndex: Int = 0,
    val pages: List<PageUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)