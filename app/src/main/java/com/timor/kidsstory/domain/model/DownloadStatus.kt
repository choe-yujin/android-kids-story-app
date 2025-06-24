package com.timor.kidsstory.domain.model

/**
 * 책 다운로드 상태
 */
enum class DownloadStatus {
    AVAILABLE,     // 다운로드 가능
    DOWNLOADING,   // 다운로드 중
    DOWNLOADED,    // 이미 다운로드됨
    FAILED         // 다운로드 실패
}

/**
 * 다운로드 진행 상태
 * @property status 다운로드 상태
 */
data class DownloadProgress(
    val status: DownloadStatus = DownloadStatus.AVAILABLE
)