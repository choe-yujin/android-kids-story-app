package com.timor.epub.model

/**
 * 스토리 메타데이터 모델
 *
 * 동화책의 메타데이터 정보를 담는 데이터 클래스입니다.
 * 앱에서 검색, 필터링, 표시 등에 필요한 모든 메타 정보를 포함합니다.
 *
 * @property storyId 스토리 고유 ID
 * @property category 스토리 카테고리 (LEGEND, FOLKTALE, CULTURE, DAILY_LIFE 등)
 * @property level 난이도 레벨 (1-5)
 * @property ageRange 권장 연령대
 * @property maker 제작자/출판사
 * @property region 지역 또는 국가
 * @property titles 다국어 제목 맵 (언어 코드 -> 제목)
 * @property tags 검색 및 필터링용 태그
 * @property size 파일 크기 (바이트)
 * @property version 버전 정보
 * @property imageCount 이미지 수
 * @property pageCount 페이지 수
 * @property estimatedReadTime 예상 읽기 시간(분)
 * @property coverImage 표지 이미지 파일명
 */
data class StoryMetadata(
    val storyId: String,
    val category: String,
    val level: Int,
    val ageRange: String,
    val maker: String,
    val region: String,
    val titles: Map<String, String>,
    val tags: List<String>,
    val size: Long,
    val version: String,
    val imageCount: Int,
    val pageCount: Int,
    val estimatedReadTime: Int,
    val coverImage: String
)