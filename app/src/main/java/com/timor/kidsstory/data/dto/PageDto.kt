package com.timor.kidsstory.data.dto

import kotlinx.serialization.Serializable

/**
 * 페이지 관련 데이터 전송 객체(DTO) 모음
 * - JSON 형식의 페이지 데이터를 파싱하기 위한 클래스
 */

/**
 * 책의 페이지 내용을 담는 응답 객체
 * - 책의 ID, 제목 및 모든 페이지 정보 포함
 *
 * @property storyId 책의 고유 ID
 * @property title 책 제목
 * @property pages 페이지 목록
 * @property contributors 제작 참여자 정보
 * @property sponsors 후원사 정보
 * @property copyright 저작권 정보
 * @property originalCopyright 원 저작권 정보
 */
@Serializable
data class PageContentResponse(
    val storyId: String,
    val title: String,
    val pages: List<PageDto>,
    val contributors: Map<String, Map<String, List<String>>>? = null,
    val sponsors: Map<String, List<String>>? = null,
    val copyright: String = "",
    val originalCopyright: String? = null,
)

/**
 * 개별 페이지의 데이터 클래스
 * - 페이지 번호와 해당 페이지의 텍스트 내용 포함
 *
 * @property pageNumber 페이지 번호 (0부터 시작)
 * @property texts 페이지에 포함된 텍스트 목록
 */
@Serializable
data class PageDto(
    val pageNumber: Int,
    val texts: List<String>
)