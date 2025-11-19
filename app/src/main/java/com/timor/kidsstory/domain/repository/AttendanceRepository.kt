package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.AttendanceStatus
import java.time.LocalDate

/**
 * 출석 관련 데이터 접근을 위한 Repository 인터페이스
 * - 사용자의 출석 정보 관리
 * - Domain Layer의 인터페이스
 */
interface AttendanceRepository {
    
    /**
     * 오늘 출석했는지 확인
     * @param date 확인할 날짜
     * @return 출석 여부
     */
    suspend fun hasAttendedToday(date: LocalDate): Boolean
    
    /**
     * 출석 체크
     * @param date 출석할 날짜
     */
    suspend fun markAttendance(date: LocalDate)
    
    /**
     * 현재 연속 출석 일수 조회
     * @return 연속 출석 일수
     */
    suspend fun getCurrentStreak(): Int
    
    /**
     * 총 출석 일수 조회
     * @return 총 출석 일수
     */
    suspend fun getTotalAttendanceDays(): Int
    
    /**
     * 오늘의 출석 상태 조회
     * @return 출석 상태 정보
     */
    suspend fun getTodayAttendanceStatus(): AttendanceStatus
    
    /**
     * 특정 기간의 출석 기록 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 출석한 날짜 목록
     */
    suspend fun getAttendanceRecords(startDate: LocalDate, endDate: LocalDate): List<LocalDate>
    
    /**
     * 최근 출석 기록 조회 (최근 30일)
     * @return 최근 출석 기록
     */
    suspend fun getRecentAttendanceRecords(): List<LocalDate>
}
