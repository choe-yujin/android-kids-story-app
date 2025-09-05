package com.timor.kidsstory.domain.model

import com.timor.kidsstory.domain.mapper.CategoryMapper

/**
 * 책의 기본 정보를 담는 도메인 모델 클래스
 *
 * @property storyId 언어별 책 식별자 (예: "801_ko-kr")
 * @property title 책 제목
 * @property coverImage 표지 이미지 경로
 * @property level 난이도 레벨 (1-5)
 * @property category 카테고리 (문자열 - 기존 호환성)
 * @property pageCount 총 페이지 수
 * @property contributors 제작 참여자 정보
 * @property sponsors 후원사 정보
 * @property copyright 저작권 정보
 * @property originalCopyright 원 저작권 정보
 * @property pages 책의 모든 페이지 정보
 * @property isDownloaded 다운로드 여부
 * @property isBookmarked 즐겨찾기 여부
 * @property downloadProgress 다운로드 진행 상태
 * @property bookVersion 책 버전
 */
data class Book(
    val storyId: String,
    val title: String,
    val coverImage: String,
    val level: Int,
    val category: String, // 기존 호환성 유지를 위해 String으로 유지
    val pageCount: Int,
    val contributors: List<Contributor>,
    val sponsors: List<String>? = null,
    val copyright: String,
    val originalCopyright: String? = null,
    val pages: List<Page>,
    val isDownloaded: Boolean = true,
    val isBookmarked: Boolean = false,
    val downloadProgress: DownloadProgress = DownloadProgress(),
    val bookVersion: Int = 1
) {
    // 새로운 Category enum으로 변환하는 속성
    val categoryEnum: Category
        get() = CategoryMapper.mapToCategory(category)
    
    // 새로운 ReadingLevel enum으로 변환하는 속성
    val readingLevel: ReadingLevel
        get() = CategoryMapper.mapToReadingLevel(level)
    
    // 책 ID 추출 (숫자)
    val bookId: Int?
        get() = storyId.split("_").firstOrNull()?.toIntOrNull()
    
    // 언어 코드 추출
    val languageCode: String
        get() = storyId.split("_").getOrNull(1)?.split("-")?.firstOrNull() ?: "ko"
}