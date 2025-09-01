package com.timor.kidsstory.domain.model

import com.timor.kidsstory.domain.model.Contributor

/**
 * 책의 기본 정보를 담는 도메인 모델 클래스
 *
 * @property storyId 책의 고유 식별자 (예: "801_ko-kr")
 * @property title 책 제목
 * @property coverImage 표지 이미지 경로
 * @property level 난이도 수준
 * @property category 카테고리 (예: "동화", "전래동화" 등)
 * @property pageCount 총 페이지 수
 * @property contributors 제작 참여자 정보
 * @property sponsors 후원사 정보
 * @property copyright 저작권 정보
 * @property originalCopyright 원 저작권 정보
 * @property pages 책의 모든 페이지 정보
 * @property isDownloaded 다운로드 여부
 * @property isBookmarked 즐겨찾기 여부
 * @property downloadProgress 다운로드 진행 상태
 */
data class Book(
    val storyId: String,
    val title: String,
    val coverImage: String,
    val level: Int,
    val category: String,
    val pageCount: Int,
    val contributors: List<Contributor>,
    val sponsors: List<String>?,
    val copyright: String,
    val originalCopyright: String?,
    val pages: List<Page>,
    val isDownloaded: Boolean = true,
    val isBookmarked: Boolean = false,
    val downloadProgress: DownloadProgress = DownloadProgress()
)