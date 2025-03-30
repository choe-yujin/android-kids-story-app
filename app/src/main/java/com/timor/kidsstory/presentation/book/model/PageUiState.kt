package com.timor.kidsstory.presentation.book.model

/**
 * 단일 페이지 UI 상태 클래스
 * - 책의 한 페이지에 대한 UI 상태 정보
 *
 * @property imageUrl 페이지 이미지 URL
 * @property texts 페이지에 표시될 텍스트 목록
 * @property pageNumber 페이지 번호 (1부터 시작 - 사용자에게 표시용)
 * @property totalPages 전체 페이지 수
 */
data class PageUiState(
    val imageUrl: String,
    val texts: List<String>,
    val pageNumber: Int,   // 1부터 시작하는 페이지 번호 (화면에 표시)
    val totalPages: Int    // 전체 페이지 수
) {
    /**
     * 페이지 표시 문자열 (예: 1/14)
     * - UI에 현재 페이지 위치를 표시하기 위한 포맷
     */
    val pageDisplay: String get() = "$pageNumber/$totalPages"
}