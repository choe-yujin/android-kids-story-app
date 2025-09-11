package com.timor.kidsstory.domain.usecase

import android.util.Log
import com.timor.kidsstory.domain.repository.UpdateCheckRepository
import javax.inject.Inject

/**
 * 업데이트 나중에 하기 UseCase
 * - 사용자가 "나중에" 버튼을 눌렀을 때 일정 시간 동안 업데이트 알림을 표시하지 않음
 */
class PostponeUpdateUseCase @Inject constructor(
    private val updateCheckRepository: UpdateCheckRepository
) {
    
    /**
     * 업데이트를 나중에 하도록 설정
     * @param versionCode 현재 표시된 업데이트 버전 코드
     * @param postponeHours 연기할 시간 (기본: 24시간)
     */
    suspend operator fun invoke(versionCode: Int, postponeHours: Long = 24L) {
        try {
            val currentTime = System.currentTimeMillis()
            val dismissUntil = currentTime + (postponeHours * 60 * 60 * 1000) // 시간을 밀리초로 변환
            
            Log.d("PostponeUpdateUseCase", "Postponing update for version $versionCode for $postponeHours hours")
            Log.d("PostponeUpdateUseCase", "Dismissed until: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dismissUntil)}")
            
            updateCheckRepository.saveDismissedUpdate(versionCode, dismissUntil)
            
        } catch (e: Exception) {
            Log.e("PostponeUpdateUseCase", "Error postponing update", e)
        }
    }
}