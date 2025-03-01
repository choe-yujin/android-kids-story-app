package com.timor.kidsstory.domain.usecase.preference

import com.timor.kidsstory.domain.model.UserPreference
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import javax.inject.Inject

/**
 * 사용자 설정을 저장하는 UseCase
 */
class SaveUserPreferenceUseCase @Inject constructor(
    private val repository: UserPreferenceRepository
) {

    // 사용자 설정 전체 저장
    suspend operator fun invoke(userPreference: UserPreference) {
        repository.saveUserPreferences(userPreference)
    }


     //언어 설정만 업데이트
    suspend fun updateLanguage(languageCode: String) {
        repository.updateLanguage(languageCode)
    }
}