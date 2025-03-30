package com.timor.kidsstory.domain.model

/**
 * 책의 기본 정보를 담는 도메인 모델 클래스
 *
 * @property storyId 책의 고유 식별자 (예: "801_ko-kr")
 * @property title 책 제목
 * @property coverImage 표지 이미지 경로
 * @property level 난이도 수준
 * @property category 카테고리 (예: "동화", "전래동화" 등)
 * @property pageCount 총 페이지 수
 * @property isDownloaded 다운로드 여부
 * @property isBookmarked 즐겨찾기 여부
 */
data class Book(
    val storyId: String,
    val title: String,
    val coverImage: String,
    val level: Int,
    val category: String,
    val pageCount: Int,
    val isDownloaded: Boolean = true,
    val isBookmarked: Boolean = false
)