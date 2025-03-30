package com.timor.kidsstory.domain.model

/**
 * 책의 한 페이지 정보를 담는 도메인 모델 클래스
 *
 * @property pageNumber 페이지 번호 (0부터 시작)
 * @property imageUrl 페이지 이미지 경로
 * @property texts 페이지에 표시될 텍스트 목록
 * @property totalPages 전체 페이지 수
 */
data class Page(
    val pageNumber: Int,
    val imageUrl: String,
    val texts: List<String>,
    val totalPages: Int = 0
) {
    // 현재 페이지 표시 형식 반환 (1/20)
    val pageDisplay: String get() = "${pageNumber + 1}/$totalPages"
    // 첫 페이지 여부 확인
    val isFirstPage: Boolean get() = pageNumber == 0
    // 마지막 페이지 여부 확인
    val isLastPage: Boolean get() = pageNumber == totalPages - 1
}