package com.timor.kidsstory.domain.model

import java.time.LocalDate

/**
 * 출석 상태 정보를 나타내는 도메인 모델
 */
data class AttendanceStatus(
    /**
     * 현재 연속 출석 일수
     */
    val currentStreak: Int = 0,
    
    /**
     * 총 출석 일수
     */
    val totalDays: Int = 0,
    
    /**
     * 오늘 출석했는지 여부
     */
    val hasAttendedToday: Boolean = false,
    
    /**
     * 마지막 출석일
     */
    val lastAttendanceDate: LocalDate? = null,
    
    /**
     * 가장 긴 연속 출석 기록
     */
    val longestStreak: Int = 0
) {
    companion object {
        /**
         * 기본 출석 상태 (처음 사용자)
         */
        fun default() = AttendanceStatus()
    }
}
