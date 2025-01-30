package com.timor.kidsstory.domain.model

data class StoryPage(
    val pageNumber: Int,
    val imageFileName: String,
    val storyTexts: List<String>  // span id별 텍스트 목록
)