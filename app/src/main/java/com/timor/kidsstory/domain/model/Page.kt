package com.timor.kidsstory.domain.model

// 페이지 도메인 모델
data class Page(
    val pageNumber: Int,
    val imageUrl: String,   // 전체 이미지 경로를 포함
    val texts: List<String>, // 페이지 내 텍스트 목록
    val totalPages: Int = 0  // 전체 페이지 수 (컬렉션의 일부로 사용될 때 선택적)
) {
    // 편의 메서드
    val pageDisplay: String get() = "${pageNumber + 1}/$totalPages"
    val isFirstPage: Boolean get() = pageNumber == 0
    val isLastPage: Boolean get() = pageNumber == totalPages - 1
}