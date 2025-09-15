package com.timor.kidsstory.domain.manager

import com.timor.kidsstory.data.local.database.dao.AttendanceDao
import com.timor.kidsstory.data.local.database.entity.AttendanceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 출석 체크 시스템을 관리하는 Manager
 * - 일별 출석 기록 및 연속 출석 일수 관리
 * - 출석 팝업 표시 여부 결정
 */
@Singleton
class AttendanceManager @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val userManager: UserManager
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _shouldShowAttendancePopup = MutableStateFlow(false)
    val shouldShowAttendancePopup: StateFlow<Boolean> = _shouldShowAttendancePopup.asStateFlow()

    /**
     * 앱 시작 시 출석 체크 처리
     * @return 첫 출석인지 여부 (팝업 표시용)
     */
    suspend fun checkTodayAttendance(): Boolean {
        val userId = userManager.getCurrentUserId()
        val today = dateFormat.format(Date())
        
        // 오늘 이미 출석했는지 확인
        val hasAttendedToday = attendanceDao.hasAttendanceOnDate(userId, today)
        
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
        val userId = userManager.getCurrentUserId()
        val today = dateFormat.format(Date())
        val yesterday = getYesterday()
        
        // 어제 출석했는지 확인
        val attendedYesterday = attendanceDao.hasAttendanceOnDate(userId, yesterday)
        
        // 연속 출석 일수 계산
        val newStreakCount = if (attendedYesterday) {
            // 어제도 출석했으면 연속 출석 +1
            val latestAttendance = attendanceDao.getLatestAttendance(userId)
            (latestAttendance?.streakCount ?: 0) + 1
        } else {
            // 어제 출석 안했으면 새로 시작 (1일차)
            1
        }
        
        // 오늘 출석 기록 저장
        val attendanceEntity = AttendanceEntity(
            userId = userId,
            date = today,
            timestamp = System.currentTimeMillis(),
            streakCount = newStreakCount
        )
        
        attendanceDao.markAttendance(attendanceEntity)
        _currentStreak.value = newStreakCount
    }

    /**
     * 현재 연속 출석 일수 업데이트
     */
    private suspend fun updateCurrentStreak() {
        val userId = userManager.getCurrentUserId()
        val latestAttendance = attendanceDao.getLatestAttendance(userId)
        
        if (latestAttendance != null) {
            val today = dateFormat.format(Date())
            
            // 최근 출석이 오늘인지 확인
            if (latestAttendance.date == today) {
                _currentStreak.value = latestAttendance.streakCount
            } else {
                // 오늘 출석 안함 - 연속 출석 끊김
                _currentStreak.value = 0
            }
        } else {
            _currentStreak.value = 0
        }
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
        val userId = userManager.getCurrentUserId()
        val latestAttendance = attendanceDao.getLatestAttendance(userId)
        val today = dateFormat.format(Date())
        
        return if (latestAttendance?.date == today) {
            latestAttendance.streakCount
        } else {
            0 // 오늘 출석 안함
        }
    }

    /**
     * 최대 연속 출석 일수 조회
     */
    suspend fun getMaxStreak(): Int {
        val userId = userManager.getCurrentUserId()
        return attendanceDao.getMaxStreak(userId) ?: 0
    }

    /**
     * 총 출석 일수 조회
     */
    suspend fun getTotalAttendanceDays(): Int {
        val userId = userManager.getCurrentUserId()
        return attendanceDao.getTotalAttendanceDays(userId)
    }

    /**
     * 어제 날짜 문자열 생성
     */
    private fun getYesterday(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(calendar.time)
    }

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
}
