package com.timor.kidsstory.domain.usecase.preference

import com.timor.kidsstory.domain.model.UserPreference
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import javax.inject.Inject

/**
 * 사용자 설정을 저장하는 UseCase
 * - 사용자 설정 정보를 저장소에 저장
 * - 전체 설정 저장 및 언어 설정만 업데이트 기능 제공
 *
 * @property repository 사용자 설정 저장소
 */
class SaveUserPreferenceUseCase @Inject constructor(
    private val repository: UserPreferenceRepository
) {

    /**
     * 사용자 설정 전체 저장
     * - 호출 방식: saveUserPreferenceUseCase(userPreference)
     *
     * @param userPreference 저장할 사용자 설정 객체
     */
    suspend operator fun invoke(userPreference: UserPreference) {
        repository.saveUserPreferences(userPreference)
    }

    /**
     * 언어 설정만 업데이트
     * - 다른 설정은 유지하고 언어만 변경
     *
     * @param languageCode 변경할 언어 코드
     */
    suspend fun updateLanguage(languageCode: String) {
        repository.updateLanguage(languageCode)
    }
}