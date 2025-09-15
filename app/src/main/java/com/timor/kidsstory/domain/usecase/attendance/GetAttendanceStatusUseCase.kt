package com.timor.kidsstory.domain.usecase.attendance

import android.util.Log
import com.timor.kidsstory.domain.manager.AttendanceManager
import javax.inject.Inject

/**
 * 출석 상태 정보를 가져오는 UseCase
 * 
 * 기존 BookshelfViewModel의 출석 관련 상태 로직을 Domain Layer로 이동
 * - 현재 연속 출석 일수
 * - 총 출석 일수  
 * - 최대 연속 출석 기록
 */
class GetAttendanceStatusUseCase @Inject constructor(
    private val attendanceManager: AttendanceManager
) {
    /**
     * 사용자의 출석 상태 정보 조회
     * 
     * @return AttendanceStatus 출석 현황 정보
     */
    suspend operator fun invoke(): AttendanceStatus {
        return try {
            Log.d("GetAttendanceStatusUseCase", "Getting attendance status")
            
            val currentStreak = attendanceManager.getCurrentStreak()
            val maxStreak = attendanceManager.getMaxStreak()
            val totalDays = attendanceManager.getTotalAttendanceDays()
            
            Log.d("GetAttendanceStatusUseCase", 
                "Attendance status - Current: $currentStreak, Max: $maxStreak, Total: $totalDays")
            
            AttendanceStatus(
                currentStreak = currentStreak,
                maxStreak = maxStreak,
                totalDays = totalDays
            )
            
        } catch (e: Exception) {
            Log.e("GetAttendanceStatusUseCase", "Error getting attendance status", e)
            AttendanceStatus() // 오류 시 기본값 반환
        }
    }
}

/**
 * 출석 상태 정보를 담는 데이터 클래스
 * 
 * @property currentStreak 현재 연속 출석 일수
 * @property maxStreak 최대 연속 출석 기록
 * @property totalDays 총 출석 일수
 */
data class AttendanceStatus(
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val totalDays: Int = 0
) {
    /**
     * 출석 레벨 계산 (1-10 단계)
     */
    val level: Int
        get() = when {
            totalDays >= 100 -> 10
            totalDays >= 80 -> 9
            totalDays >= 60 -> 8
            totalDays >= 45 -> 7
            totalDays >= 30 -> 6
            totalDays >= 20 -> 5
            totalDays >= 14 -> 4
            totalDays >= 7 -> 3
            totalDays >= 3 -> 2
            totalDays >= 1 -> 1
            else -> 0
        }
    
    /**
     * 출석률 계산 (앱 사용 기간 대비)
     */
    fun getAttendanceRate(appUsageDays: Int): Float {
        return if (appUsageDays > 0) {
            totalDays.toFloat() / appUsageDays.toFloat()
        } else {
            0f
        }
    }
    
    /**
     * 다음 레벨까지 필요한 출석 일수
     */
    val daysToNextLevel: Int
        get() {
            val nextLevelThreshold = when (level) {
                0 -> 1
                1 -> 3
                2 -> 7
                3 -> 14
                4 -> 20
                5 -> 30
                6 -> 45
                7 -> 60
                8 -> 80
                9 -> 100
                else -> 0 // 최고 레벨
            }
            return if (nextLevelThreshold > 0) {
                nextLevelThreshold - totalDays
            } else {
                0
            }
        }
}