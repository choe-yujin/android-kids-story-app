package com.timor.kidsstory.data.repository

import com.timor.kidsstory.data.local.database.dao.AttendanceDao
import com.timor.kidsstory.data.local.database.entity.AttendanceEntity
import com.timor.kidsstory.domain.model.AttendanceStatus
import com.timor.kidsstory.domain.repository.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AttendanceRepository 구현체
 * - Room Database를 통한 출석 데이터 관리
 * - 기본 사용자("default_user") 지원
 */
@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao
) : AttendanceRepository {
    
    companion object {
        private const val DEFAULT_USER_ID = "default_user"
    }
    
    override suspend fun hasAttendedToday(date: LocalDate): Boolean = withContext(Dispatchers.IO) {
        val dateString = date.toString()
        attendanceDao.getAttendanceByUserAndDate(DEFAULT_USER_ID, dateString) != null
    }
    
    override suspend fun markAttendance(date: LocalDate): Unit = withContext(Dispatchers.IO) {
        val dateString = date.toString()
        val existingAttendance = attendanceDao.getAttendanceByUserAndDate(DEFAULT_USER_ID, dateString)
        
        if (existingAttendance == null) {
            // 새로운 출석 기록 생성
            val currentStreak = calculateNewStreak(date)
            val attendance = AttendanceEntity.fromDate(
                userId = DEFAULT_USER_ID,
                date = date,
                streakCount = currentStreak
            )
            attendanceDao.insertAttendance(attendance)
        }
    }
    
    override suspend fun getCurrentStreak(): Int = withContext(Dispatchers.IO) {
        val allAttendances = attendanceDao.getAllAttendancesByUser(DEFAULT_USER_ID)
        if (allAttendances.isEmpty()) return@withContext 0
        
        // 날짜 순으로 정렬된 출석 기록에서 현재 연속 출석 계산
        val sortedDates = allAttendances.map { it.toLocalDate() }.sortedDescending()
        val today = LocalDate.now()
        
        // 오늘부터 역순으로 연속성 확인
        var streak = 0
        var checkDate = today
        
        for (attendanceDate in sortedDates) {
            if (attendanceDate == checkDate) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else if (attendanceDate == checkDate.minusDays(1) && streak == 0) {
                // 오늘 출석하지 않았지만 어제까지는 연속 출석
                streak++
                checkDate = attendanceDate.minusDays(1)
            } else {
                break
            }
        }
        
        streak
    }
    
    override suspend fun getTotalAttendanceDays(): Int = withContext(Dispatchers.IO) {
        attendanceDao.getTotalAttendanceCountByUser(DEFAULT_USER_ID)
    }
    
    override suspend fun getTodayAttendanceStatus(): AttendanceStatus = withContext(Dispatchers.IO) {
        val today = LocalDate.now()
        val hasAttendedToday = hasAttendedToday(today)
        val currentStreak = getCurrentStreak()
        val totalDays = getTotalAttendanceDays()
        val lastAttendance = attendanceDao.getLastAttendanceByUser(DEFAULT_USER_ID)
        val longestStreak = attendanceDao.getMaxStreakByUser(DEFAULT_USER_ID) ?: 0
        
        AttendanceStatus(
            currentStreak = currentStreak,
            totalDays = totalDays,
            hasAttendedToday = hasAttendedToday,
            lastAttendanceDate = lastAttendance?.toLocalDate(),
            longestStreak = longestStreak
        )
    }
    
    override suspend fun getAttendanceRecords(
        startDate: LocalDate, 
        endDate: LocalDate
    ): List<LocalDate> = withContext(Dispatchers.IO) {
        val startDateString = startDate.toString()
        val endDateString = endDate.toString()
        
        attendanceDao.getAttendancesBetweenByUser(DEFAULT_USER_ID, startDateString, endDateString)
            .map { it.toLocalDate() }
    }
    
    override suspend fun getRecentAttendanceRecords(): List<LocalDate> = withContext(Dispatchers.IO) {
        attendanceDao.getRecentAttendancesByUser(DEFAULT_USER_ID, 30)
            .map { it.toLocalDate() }
            .sortedDescending()
    }
    
    /**
     * 새로운 출석 시 연속 출석 일수 계산
     */
    private suspend fun calculateNewStreak(newDate: LocalDate): Int {
        val yesterday = newDate.minusDays(1)
        val yesterdayAttendance = attendanceDao.getAttendanceByUserAndDate(DEFAULT_USER_ID, yesterday.toString())
        
        return if (yesterdayAttendance != null) {
            // 어제도 출석했다면 연속 일수 +1
            yesterdayAttendance.streakCount + 1
        } else {
            // 어제 출석하지 않았다면 새로운 연속 시작
            1
        }
    }
}
