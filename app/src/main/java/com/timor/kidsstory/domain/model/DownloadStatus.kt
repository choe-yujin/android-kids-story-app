package com.timor.kidsstory.domain.model

/**
 * 책 다운로드 상태
 */
enum class DownloadStatus {
    AVAILABLE,     // 다운로드 가능
    DOWNLOADING,   // 다운로드 중
    DOWNLOADED,    // 이미 다운로드됨
    UPDATE_AVAILABLE, // 업데이트 가능
    FAILED         // 다운로드 실패
}

/**
 * 다운로드 진행 상태
 * @property status 다운로드 상태
 * @property progress 진행률 (0.0 ~ 1.0)
 * @property downloadedSize 다운로드된 크기 (바이트)
 * @property totalSize 전체 크기 (바이트)
 * @property errorMessage 에러 메시지 (실패 시)
 */
data class DownloadProgress(
    val status: DownloadStatus = DownloadStatus.AVAILABLE,
    val progress: Float = 0f,
    val downloadedSize: Long = 0L,
    val totalSize: Long = 0L,
    val errorMessage: String? = null
) {
    /**
     * 다운로드 완료 여부
     */
    val isCompleted: Boolean
        get() = status == DownloadStatus.DOWNLOADED
    
    /**
     * 다운로드 중 여부
     */
    val isDownloading: Boolean
        get() = status == DownloadStatus.DOWNLOADING
    
    /**
     * 다운로드 실패 여부
     */
    val isFailed: Boolean
        get() = status == DownloadStatus.FAILED
}