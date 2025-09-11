package com.timor.kidsstory.domain.model

/**
 * 업데이트 체크 설정 모델
 */
data class UpdateCheckSettings(
    val lastUpdateCheckTime: Long = 0L,           // 마지막 업데이트 체크 시간
    val lastDismissedVersion: Int = 0,            // 마지막에 "나중에"를 누른 버전 코드
    val dismissedUntil: Long = 0L,                // 다시 묻지 않을 시간까지
    val checkIntervalHours: Long = 24L            // 체크 간격 (기본: 24시간)
)
