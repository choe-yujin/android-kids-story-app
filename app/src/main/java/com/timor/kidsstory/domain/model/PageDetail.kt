package com.timor.kidsstory.domain.model

// 페이지 상세 정보 도메인 모델
data class PageDetail(
    val imageUrl: String,
    val texts: List<String>,
    val pageNumber: Int
)