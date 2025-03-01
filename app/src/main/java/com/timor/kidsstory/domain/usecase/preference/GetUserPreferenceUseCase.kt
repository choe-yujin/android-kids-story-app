package com.timor.kidsstory.domain.usecase.preference

import com.timor.kidsstory.domain.model.UserPreference
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 사용자 설정을 가져오는 UseCase
 */
class GetUserPreferenceUseCase @Inject constructor(
    private val repository: UserPreferenceRepository
) {

    // 사용자 설정을 Flow로 제공
    operator fun invoke(): Flow<UserPreference> {
        return repository.getUserPreferences()
    }
}