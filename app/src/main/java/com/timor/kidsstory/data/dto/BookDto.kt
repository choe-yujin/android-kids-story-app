package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

/**
 * 책 관련 데이터 전송 객체(DTO) 모음
 * - 앱 내부 Assets 및 원격 API에서 받은 JSON 데이터를 파싱하기 위한 클래스
 */

/**
 * 여러 책 정보를 담고 있는 응답 객체
 * - stories-metadata.json 파일의 최상위 객체에 매핑
 *
 * @property stories 책 목록
 */
@Serializable
data class StoriesResponse(
    val stories: List<BookDto>
)

/**
 * 개별 책의 메타데이터 정보
 * - 책의 기본 정보, 제목, 커버 이미지 등 포함
 *
 * @property storyId 책의 고유 ID (예: 801_en-ph)
 * @property ageRange 추천 연령대
 * @property imageCount 책에 포함된 이미지 수
 * @property pageCount 책의 전체 페이지 수
 * @property level 난이도 레벨
 * @property maker 제작자 정보
 * @property titles 다국어 제목 모음
 * @property version 버전 정보
 * @property tags 태그 목록 (카테고리, 키워드 등)
 * @property size 파일 크기
 * @property coverImage 커버 이미지 파일명
 * @property category 카테고리 (예: 전래동화, 창작동화 등)
 * @property region 지역 정보
 * @property estimatedReadTime 예상 읽기 시간(분)
 */
@Serializable
data class BookDto(
    val storyId: String,
    val ageRange: String,
    val imageCount: Int,
    val pageCount: Int,
    val level: Int,
    val maker: String,
    val titles: TitlesDto,
    val version: String,
    val tags: List<String>,
    val size: Long,
    val coverImage: String,
    val category: String,
    val region: String,
    val estimatedReadTime: Int
)

/**
 * 다국어 제목 정보
 * - 각 언어별 책 제목 저장
 *
 * @property tet 테툼어 제목
 * @property ko 한국어 제목
 * @property en 영어 제목
 */
@Serializable
data class TitlesDto(
    val tet: String,
    val ko: String,
    val en: String
)