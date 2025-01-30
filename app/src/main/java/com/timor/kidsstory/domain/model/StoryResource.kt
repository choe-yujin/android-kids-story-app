package com.timor.kidsstory.domain.model

// 스토리 리소스 도메인 모델
data class StoryResource(
    val storyId: String,      // epub 파일명
    val title: String,        // 책 제목
    val coverImage: String,   // 표지 이미지 파일명 (예: "cover_819_en.jpg")
    val pages: List<Page>
)