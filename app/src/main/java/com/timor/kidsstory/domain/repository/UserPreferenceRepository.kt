package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.UserPreference
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 설정 관련 저장소 인터페이스
 */
interface UserPreferenceRepository {


    // 저장된 사용자 설정을 Flow로 제공
    // 설정이 변경될 때마다 Flow가 새로운 값을 방출
    fun getUserPreferences(): Flow<UserPreference>

    // 사용자 설정 전체 저장
    suspend fun saveUserPreferences(userPreference: UserPreference)

    // 언어 코드만 업데이트
    suspend fun updateLanguage(languageCode: String)

    // 배경음 설정 추가
    suspend fun updateMusicSetting(isMusicOn: Boolean)
}