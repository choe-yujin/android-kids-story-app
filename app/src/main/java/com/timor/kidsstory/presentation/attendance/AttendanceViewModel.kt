package com.timor.kidsstory.presentation.attendance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.manager.AttendanceManager
import com.timor.kidsstory.domain.usecase.attendance.CheckTodayAttendanceUseCase
import com.timor.kidsstory.domain.usecase.attendance.GetAttendanceStatusUseCase
import com.timor.kidsstory.domain.usecase.attendance.AttendanceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 출석 관리 전용 ViewModel
 * 
 * 단일 책임: 출석 체크 UI 상태 관리
 * - 출석 팝업 표시/숨김
 * - 연속 출석 일수 관리
 * - 출석 상태 조회
 */
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceManager: AttendanceManager,
    private val checkTodayAttendanceUseCase: CheckTodayAttendanceUseCase,
    private val getAttendanceStatusUseCase: GetAttendanceStatusUseCase
) : ViewModel() {

    // 출석 UI 상태
    private val _attendanceState = MutableStateFlow(AttendanceUiState())
    val attendanceState: StateFlow<AttendanceUiState> = _attendanceState.asStateFlow()

    // 출석 팝업 표시 여부
    private val _shouldShowAttendancePopup = MutableStateFlow(false)
    val shouldShowAttendancePopup: StateFlow<Boolean> = _shouldShowAttendancePopup.asStateFlow()

    init {
        initializeAttendance()
        observeAttendanceManager()
    }

    /**
     * 앱 시작 시 출석 초기화
     */
    private fun initializeAttendance() {
        viewModelScope.launch {
            try {
                Log.d("AttendanceViewModel", "Initializing attendance check")
                
                // UseCase를 통한 출석 체크
                val isNewAttendance = checkTodayAttendanceUseCase()
                
                Log.d("AttendanceViewModel", "checkTodayAttendanceUseCase returned: $isNewAttendance")
                if (isNewAttendance) {
                    _shouldShowAttendancePopup.value = true
                    Log.d("AttendanceViewModel", "New attendance detected - showing popup")
                }
                
                // 현재 출석 상태 조회
                val attendanceStatus = getAttendanceStatusUseCase()
                Log.d("AttendanceViewModel", "getAttendanceStatusUseCase returned: $attendanceStatus")
                _attendanceState.update { currentState ->
                    currentState.copy(
                        currentStreak = attendanceStatus.currentStreak,
                        totalAttendanceDays = attendanceStatus.totalDays,
                        longestStreak = attendanceStatus.maxStreak,  // maxStreak -> longestStreak 매핑
                        hasAttendedToday = true,
                        isLoading = false
                    )
                }
                
                Log.d("AttendanceViewModel", "Attendance status updated: streak=${attendanceStatus.currentStreak}")
                
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Error initializing attendance", e)
                _attendanceState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * AttendanceManager의 상태 변화 관찰
     */
    private fun observeAttendanceManager() {
        // Manager의 연속 출석 일수 변화 관찰
        viewModelScope.launch {
            attendanceManager.currentStreak.collect { streak ->
                _attendanceState.update { it.copy(currentStreak = streak) }
                Log.d("AttendanceViewModel", "Streak updated from manager: $streak")
            }
        }

        // Manager의 팝업 표시 상태 관찰
        viewModelScope.launch {
            attendanceManager.shouldShowAttendancePopup.collect { shouldShow ->
                _shouldShowAttendancePopup.value = shouldShow
                Log.d("AttendanceViewModel", "Popup state updated from manager: $shouldShow")
            }
        }
    }

    /**
     * 출석 팝업 닫기
     */
    fun onAttendancePopupDismiss() {
        Log.d("AttendanceViewModel", "Attendance popup dismissed")
        _shouldShowAttendancePopup.value = false
        attendanceManager.onAttendancePopupShown()
    }

    /**
     * 수동 출석 새로고침
     */
    fun refreshAttendanceStatus() {
        viewModelScope.launch {
            try {
                _attendanceState.update { it.copy(isLoading = true) }
                
                val attendanceStatus = getAttendanceStatusUseCase()
                _attendanceState.update { currentState ->
                    currentState.copy(
                        currentStreak = attendanceStatus.currentStreak,
                        totalAttendanceDays = attendanceStatus.totalDays,
                        longestStreak = attendanceStatus.maxStreak,  // maxStreak -> longestStreak 매핑
                        isLoading = false,
                        error = null
                    )
                }
                
                Log.d("AttendanceViewModel", "Attendance status refreshed")
                
            } catch (e: Exception) {
                Log.e("AttendanceViewModel", "Error refreshing attendance status", e)
                _attendanceState.update { 
                    it.copy(isLoading = false, error = "출석 정보를 불러올 수 없습니다: ${e.message}")
                }
            }
        }
    }

    /**
     * 테스트용 팝업 강제 표시
     */
    fun showAttendancePopupForTest() {
        _shouldShowAttendancePopup.value = true
        attendanceManager.showAttendancePopup()
    }

    /**
     * 연속 출석 일수 포맷팅
     */
    fun formatStreakDisplay(streak: Int): String {
        return attendanceManager.formatStreakForDisplay(streak)
    }
}

/**
 * 출석 UI 상태 데이터 클래스
 */
data class AttendanceUiState(
    val currentStreak: Int = 0,           // 현재 연속 출석 일수
    val totalAttendanceDays: Int = 0,     // 총 출석 일수
    val longestStreak: Int = 0,           // 최대 연속 출석 기록
    val hasAttendedToday: Boolean = false, // 오늘 출석 여부
    val isLoading: Boolean = true,        // 로딩 상태
    val error: String? = null             // 에러 메시지
) {
    /**
     * 출석 레벨 계산 (출석 일수 기반)
     */
    val attendanceLevel: AttendanceLevel
        get() = when {
            currentStreak >= 30 -> AttendanceLevel.MASTER
            currentStreak >= 15 -> AttendanceLevel.EXPERT
            currentStreak >= 7 -> AttendanceLevel.REGULAR
            currentStreak >= 3 -> AttendanceLevel.BEGINNER
            else -> AttendanceLevel.STARTER
        }

    /**
     * 출석 격려 메시지
     */
    val encouragementMessage: String
        get() = when (attendanceLevel) {
            AttendanceLevel.STARTER -> "출석을 시작해보세요!"
            AttendanceLevel.BEGINNER -> "좋은 시작이에요!"
            AttendanceLevel.REGULAR -> "꾸준히 잘하고 있어요!"
            AttendanceLevel.EXPERT -> "정말 대단해요!"
            AttendanceLevel.MASTER -> "출석 마스터!"
        }
}

/**
 * 출석 레벨 enum
 */
enum class AttendanceLevel {
    STARTER,    // 0-2일
    BEGINNER,   // 3-6일
    REGULAR,    // 7-14일
    EXPERT,     // 15-29일
    MASTER      // 30일 이상
}
