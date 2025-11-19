package com.timor.kidsstory.domain.manager

import com.timor.kidsstory.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 출석 체크 시스템을 관리하는 Manager
 * - AttendanceRepository를 통한 출석 관리
 * - UI 상태 관리 (팝업 표시 등)
 */
@Singleton
class AttendanceManager @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _shouldShowAttendancePopup = MutableStateFlow(false)
    val shouldShowAttendancePopup: StateFlow<Boolean> = _shouldShowAttendancePopup.asStateFlow()

    /**
     * 앱 시작 시 출석 체크 처리
     * @return 첫 출석인지 여부 (팝업 표시용)
     */
    suspend fun checkTodayAttendance(): Boolean {
        val today = LocalDate.now()
        
        // 오늘 이미 출석했는지 확인
        val hasAttendedToday = attendanceRepository.hasAttendedToday(today)
        
        return if (!hasAttendedToday) {
            // 오늘 첫 출석 처리
            markTodayAttendance()
            _shouldShowAttendancePopup.value = true
            true // 첫 출석
        } else {
            // 이미 출석함 - 현재 연속 출석 일수만 업데이트
            updateCurrentStreak()
            false // 이미 출석함
        }
    }

    /**
     * 오늘 출석 처리
     */
    private suspend fun markTodayAttendance() {
        val today = LocalDate.now()
        
        // 출석 기록 저장
        attendanceRepository.markAttendance(today)
        
        // 현재 연속 출석 일수 업데이트
        val currentStreak = attendanceRepository.getCurrentStreak()
        _currentStreak.value = currentStreak
    }

    /**
     * 현재 연속 출석 일수 업데이트
     */
    private suspend fun updateCurrentStreak() {
        val currentStreak = attendanceRepository.getCurrentStreak()
        _currentStreak.value = currentStreak
    }

    /**
     * 출석 팝업 표시 완료 처리
     */
    fun onAttendancePopupShown() {
        _shouldShowAttendancePopup.value = false
    }

    /**
     * 연속 출석 일수 조회
     */
    suspend fun getCurrentStreak(): Int {
        return attendanceRepository.getCurrentStreak()
    }

    /**
     * 최대 연속 출석 일수 조회
     */
    suspend fun getMaxStreak(): Int {
        val attendanceStatus = attendanceRepository.getTodayAttendanceStatus()
        return attendanceStatus.longestStreak
    }

    /**
     * 총 출석 일수 조회
     */
    suspend fun getTotalAttendanceDays(): Int {
        return attendanceRepository.getTotalAttendanceDays()
    }

    /**
     * 출석 상태 정보 조회
     */
    suspend fun getAttendanceStatus() = attendanceRepository.getTodayAttendanceStatus()

    /**
     * 연속 출석 일수 포맷팅 (UI 표시용)
     * @return "1day", "2days", "15days" 등
     */
    fun formatStreakForDisplay(streak: Int): String {
        return when (streak) {
            0 -> "0day"
            1 -> "1day"
            else -> "${streak}days"
        }
    }
    
    /**
     * 출석 팝업 강제 표시 (테스트용)
     */
    fun showAttendancePopup() {
        _shouldShowAttendancePopup.value = true
    }
}
