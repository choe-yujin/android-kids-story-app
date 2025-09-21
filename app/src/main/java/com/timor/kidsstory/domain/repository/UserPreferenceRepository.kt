package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.UserPreference
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 설정 관련 저장소 인터페이스
 * - 사용자 선호도 및 설정을 영구 저장소에 저장하고 가져오는 기능 정의
 * - 언어 설정, 음악 설정 등 사용자 기본 설정 관리
 */
interface UserPreferenceRepository {

    /**
     * 저장된 사용자 설정을 Flow로 제공
     * - 설정이 변경될 때마다 Flow가 새로운 값을 방출
     * - UI에서 사용자 설정 변경사항을 실시간으로 관찰 가능
     *
     * @return 사용자 설정 Flow
     */
    fun getUserPreferences(): Flow<UserPreference>

    /**
     * 사용자 설정 전체 저장
     *
     * @param userPreference 저장할 사용자 설정 객체
     */
    suspend fun saveUserPreferences(userPreference: UserPreference)

    /**
     * 언어 코드만 업데이트
     * - 다른 설정은 유지하면서 언어만 변경
     *
     * @param languageCode 변경할 언어 코드
     */
    suspend fun updateLanguage(languageCode: String)

    /**
     * 배경음 설정 업데이트
     * - 다른 설정은 유지하면서 배경음 설정만 변경
     *
     * @param isMusicOn 배경음 켜기/끄기 상태
     */
    suspend fun updateMusicSetting(isMusicOn: Boolean)

    /**
     * 배경음악 음량 업데이트
     * - 다른 설정은 유지하면서 배경음악 음량만 변경
     *
     * @param volume 배경음악 음량 (0.0 ~ 1.0)
     */
    suspend fun updateMusicVolume(volume: Float)

    /**
     * 효과음 설정 업데이트
     * - 다른 설정은 유지하면서 효과음 설정만 변경
     *
     * @param isSoundEffectOn 효과음 켜기/끄기 상태
     */
    suspend fun updateSoundEffectSetting(isSoundEffectOn: Boolean)

    /**
     * 효과음 음량 업데이트
     * - 다른 설정은 유지하면서 효과음 음량만 변경
     *
     * @param volume 효과음 음량 (0.0 ~ 1.0)
     */
    suspend fun updateSoundEffectVolume(volume: Float)
    
    /**
     * 첫 실행 상태 업데이트
     * - 첫 실행 완료 처리
     */
    suspend fun markFirstRunComplete()
    
    /**
     * 선택된 언어와 레벨 업데이트
     * - 레벨 테스트 완료 후 언어와 레벨을 함께 저장
     *
     * @param languageCode 선택된 언어 코드
     * @param level 측정된 또는 선택된 레벨
     * @param hasCompletedTest 레벨 테스트 완료 여부
     */
    suspend fun updateLanguageAndLevel(
        languageCode: String, 
        level: Int, 
        hasCompletedTest: Boolean = true
    )
}