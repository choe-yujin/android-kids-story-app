package com.timor.kidsstory.domain.model

// 스토리 상세 정보 도메인 모델
data class StoryDetail(
    val currentPage: Int,
    val pages: List<PageDetail>
)
