package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.UpdateCheckSettings
import kotlinx.coroutines.flow.Flow

/**
 * 업데이트 체크 설정 관리 리포지토리
 */
interface UpdateCheckRepository {
    /**
     * 업데이트 체크 설정 가져오기
     */
    fun getUpdateCheckSettings(): Flow<UpdateCheckSettings>
    
    /**
     * 마지막 업데이트 체크 시간 저장
     */
    suspend fun updateLastCheckTime(time: Long)
    
    /**
     * "나중에" 버튼 클릭 시 설정 저장
     */
    suspend fun saveDismissedUpdate(versionCode: Int, dismissUntil: Long)
    
    /**
     * 업데이트 체크가 필요한지 확인
     */
    suspend fun shouldCheckForUpdate(currentVersionCode: Int): Boolean
}
