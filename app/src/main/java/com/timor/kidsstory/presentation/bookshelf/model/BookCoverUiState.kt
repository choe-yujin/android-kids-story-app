package com.timor.kidsstory.presentation.bookshelf.model

import com.timor.kidsstory.domain.model.DownloadStatus

/**
 * 책 표지 UI 상태 클래스
 * - 책장 화면에서 표시되는 개별 책 표지의 상태 정보
 *
 * @property imageUrl 책 표지 이미지 URL
 * @property title 책 제목
 * @property storyId 책 고유 ID (예: 801_en-ph)
 * @property downloadStatus 다운로드 상태
 * @property remoteId 원격 저장소의 책 ID
 * @property level 책 난이도 레벨
 * @property category 책 카테고리
 */
data class BookCoverUiState(
    val imageUrl: String,
    val title: String,
    val storyId: String,
    val downloadStatus: DownloadStatus = DownloadStatus.AVAILABLE,
    val remoteId: Int? = null,
    val level: Int = 1,
    val category: String = ""
)