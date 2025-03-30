package com.timor.kidsstory.domain.usecase

import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 배경 음악 설정 관련 유스케이스
 * - 배경 음악의 상태 관리 및 변경 기능 제공
 * - 사용자 설정에서 음악 상태를 읽고 업데이트
 *
 * @property userPreferenceRepository 사용자 설정 저장소
 */
class MusicSettingUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) {
    /**
     * 배경 음악 상태 Flow
     * - 사용자 설정의 isMusicOn 값을 관찰
     * - 설정 변경 시 자동으로 업데이트된 값 제공
     */
    val isMusicOn: Flow<Boolean> = userPreferenceRepository.getUserPreferences().map {
        it.isMusicOn
    }

    /**
     * 배경 음악 상태 토글
     * - 현재 상태를 반전시켜 저장
     *
     * @param currentState 현재 음악 상태
     */
    suspend fun toggleMusicSetting(currentState: Boolean) {
        userPreferenceRepository.updateMusicSetting(currentState)
    }

}