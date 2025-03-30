package com.timor.epub.model

/**
 * 스토리 번역 데이터 모델
 *
 * 특정 언어로 된 스토리의 번역 정보를 담는 데이터 클래스입니다.
 * 스토리 ID, 해당 언어의 제목, 그리고 번역된 페이지 정보를 포함합니다.
 *
 * @property storyId 스토리 고유 ID
 * @property title 해당 언어로 된 스토리 제목
 * @property pages 번역된 페이지 목록
 */
data class StoryTranslation(
    val storyId: String,
    val title: String,
    val pages: List<Page>
)