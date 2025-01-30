package com.timor.kidsstory.domain.model

// 페이지 도메인 모델
data class Page(
    val pageNumber: Int,
    val imageFileName: String,
    val storyTexts: List<String>  // span id별 텍스트 목록
)