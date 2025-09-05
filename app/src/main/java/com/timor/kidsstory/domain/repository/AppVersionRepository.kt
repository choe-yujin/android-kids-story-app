package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.AppVersionInfo

/**
 * 앱 버전 관련 저장소 인터페이스
 */
interface AppVersionRepository {
    /**
     * 최신 버전 정보 가져오기
     */
    suspend fun getLatestVersionInfo(): Result<AppVersionInfo>
    
    /**
     * 현재 앱 버전 코드 가져오기
     */
    fun getCurrentVersionCode(): Int
    
    /**
     * 현재 앱 버전 이름 가져오기
     */
    fun getCurrentVersionName(): String
}