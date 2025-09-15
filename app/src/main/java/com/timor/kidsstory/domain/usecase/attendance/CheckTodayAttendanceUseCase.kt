package com.timor.kidsstory.domain.usecase.attendance

import android.util.Log
import com.timor.kidsstory.domain.manager.AttendanceManager
import javax.inject.Inject

/**
 * 오늘 출석 체크를 처리하는 UseCase
 * 
 * 기존 BookshelfViewModel의 출석 체크 로직을 Domain Layer로 이동
 * - 앱 시작 시 자동 출석 체크
 * - 출석 팝업 표시 여부 결정
 */
class CheckTodayAttendanceUseCase @Inject constructor(
    private val attendanceManager: AttendanceManager
) {
    /**
     * 오늘 출석 체크 수행
     * 
     * @return 새로운 출석인지 여부 (true = 새 출석, false = 이미 출석함)
     */
    suspend operator fun invoke(): Boolean {
        return try {
            Log.d("CheckTodayAttendanceUseCase", "Checking today's attendance")
            
            val isNewAttendance = attendanceManager.checkTodayAttendance()
            
            if (isNewAttendance) {
                Log.d("CheckTodayAttendanceUseCase", "New attendance recorded for today")
            } else {
                Log.d("CheckTodayAttendanceUseCase", "Already attended today")
            }
            
            isNewAttendance
            
        } catch (e: Exception) {
            Log.e("CheckTodayAttendanceUseCase", "Error checking attendance", e)
            false // 오류 시 새 출석으로 간주하지 않음
        }
    }
}