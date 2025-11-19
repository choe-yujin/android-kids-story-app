package com.timor.kidsstory.presentation.book.model

import com.timor.kidsstory.domain.model.Contributor

/**
 * 단일 페이지 UI 상태 클래스
 * - 책의 한 페이지에 대한 UI 상태 정보
 *
 * @property imageUrl 페이지 이미지 URL
 * @property texts 페이지에 표시될 텍스트 목록
 * @property pageNumber 페이지 번호 (1부터 시작 - 사용자에게 표시용)
 * @property totalPages 전체 페이지 수
 * @property textSectionState 텍스트 섹션의 스크롤 상태
 * @property contributors 제작 참여자 정보 (첫 페이지에만 해당)
 * @property sponsors 후원사 정보 (첫 페이지에만 해당)
 * @property copyright 저작권 정보 (첫 페이지에만 해당)
 * @property originalCopyright 원 저작권 정보 (첫 페이지에만 해당)
 * @property title 책 제목 (첫 페이지 저작권 표시에 사용)
 */
data class PageUiState(
    val imageUrl: String,
    val texts: List<String>,
    val pageNumber: Int,   // 1부터 시작하는 페이지 번호 (화면에 표시)
    val totalPages: Int,    // 전체 페이지 수
    val pageType: String = "SPLIT", // 페이지 레이아웃 타입
    val textSectionState: PageTextSectionUiState = PageTextSectionUiState(),
    val contributors: List<Contributor> = emptyList(),
    val sponsors: List<String>? = null,
    val copyright: String = "",
    val originalCopyright: String? = null,
    val title: String = "",
    val currentLanguageCode: String = "", // Added for TTS button visibility
) {
    /**
     * 페이지 표시 문자열 (예: 1/14)
     * - UI에 현재 페이지 위치를 표시하기 위한 포맷
     */
    val pageDisplay: String get() = "$pageNumber/$totalPages"
}