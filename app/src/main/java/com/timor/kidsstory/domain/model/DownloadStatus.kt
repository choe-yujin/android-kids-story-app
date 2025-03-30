package com.timor.kidsstory.domain.model

/**
 * 책 다운로드 상태
 */
enum class DownloadStatus {
    AVAILABLE,    // 다운로드 가능
    DOWNLOADING,  // 다운로드 진행 중
    DOWNLOADED,   // 다운로드 완료
    FAILED        // 다운로드 실패
}