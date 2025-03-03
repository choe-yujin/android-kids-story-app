package com.timor.kidsstory.domain.usecase

import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicSettingUseCase @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
) {
    val isMusicOn: Flow<Boolean> = userPreferenceRepository.getUserPreferences().map {
        it.isMusicOn
    }

    suspend fun toggleMusicSetting(currentState: Boolean) {
        userPreferenceRepository.updateMusicSetting(currentState)
    }

}