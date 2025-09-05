package com.timor.kidsstory.domain.model

/**
 * 앱 버전 정보 도메인 모델
 * 
 * @property latestVersionCode 최신 버전 코드
 * @property latestVersionName 최신 버전 이름 (예: "1.1.1")
 * @property updateMessage 업데이트 메시지 (현재 언어로 표시)
 * @property downloadUrl 다운로드 URL
 * @property isUpdateRequired 필수 업데이트 여부
 * @property releaseNotes 릴리즈 노트 (선택사항)
 */
data class AppVersionInfo(
    val latestVersionCode: Int,
    val latestVersionName: String,
    val updateMessage: String,
    val downloadUrl: String,
    val isUpdateRequired: Boolean = false,
    val releaseNotes: String? = null
)