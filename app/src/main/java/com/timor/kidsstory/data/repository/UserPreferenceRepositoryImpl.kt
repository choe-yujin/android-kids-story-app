package com.timor.kidsstory.data.repository

import android.content.Context
import androidx.core.content.edit
import com.timor.kidsstory.domain.model.UserPreference
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserPreferenceRepository 인터페이스의 구현체
 * SharedPreferences를 사용하여 사용자 설정을 저장하고 불러옴
 */
@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferenceRepository {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    // 메모리에 캐싱된 설정
    private val _userPreferencesFlow = MutableStateFlow(loadFromPreferences())

    override fun getUserPreferences(): Flow<UserPreference> {
        return _userPreferencesFlow.asStateFlow()
    }

    override suspend fun saveUserPreferences(userPreference: UserPreference) {
        // SharedPreferences에 저장
        prefs.edit {
            putString(KEY_LANGUAGE, userPreference.languageCode)
        }

        // 메모리 캐시 업데이트
        _userPreferencesFlow.value = userPreference
    }

    override suspend fun updateLanguage(languageCode: String) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(languageCode = languageCode)
        saveUserPreferences(newPrefs)
    }

    // SharedPreferences에서 설정 로드
    private fun loadFromPreferences(): UserPreference {
        return UserPreference(
            languageCode = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        )
    }

    companion object {
        private const val PREFERENCES_NAME = "tetum_dreams_preferences"
        private const val KEY_LANGUAGE = "language_code"
        private const val DEFAULT_LANGUAGE = "en-ph"
    }
}