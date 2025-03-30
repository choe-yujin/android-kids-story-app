package com.timor.kidsstory.data.repository

import android.content.Context
import android.provider.Contacts.SettingsColumns.KEY
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
 * 사용자 설정 저장소 인터페이스의 구현체
 * - 앱 설정 데이터에 대한 CRUD 작업 수행
 * - SharedPreferences를 사용하여 설정을 영구 저장
 * - 메모리 캐시와 Flow를 활용하여 실시간 데이터 제공
 *
 * @property context 애플리케이션 컨텍스트
 */
@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferenceRepository {

    // 앱 전용 SharedPreferences 인스턴스 생성
    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    // 메모리에 캐싱된 설정 - 빠른 접근을 위해 유지
    private val _userPreferencesFlow = MutableStateFlow(loadFromPreferences())

    /**
     * 사용자 설정 가져오기
     * - 메모리 캐시된 설정을 Flow로 제공
     * - UI에서 실시간으로 설정 변경 관찰 가능
     *
     * @return 사용자 설정 Flow
     */
    override fun getUserPreferences(): Flow<UserPreference> {
        return _userPreferencesFlow.asStateFlow()
    }

    /**
     * 사용자 설정 전체 저장
     * - SharedPreferences에 설정 저장
     * - 메모리 캐시 업데이트
     *
     * @param userPreference 저장할 사용자 설정 객체
     */
    override suspend fun saveUserPreferences(userPreference: UserPreference) {
        // SharedPreferences에 저장
        prefs.edit {
            putString(KEY_LANGUAGE, userPreference.languageCode)
            putBoolean(KEY_MUSIC_ON, userPreference.isMusicOn)
        }

        // 메모리 캐시 업데이트
        _userPreferencesFlow.value = userPreference
    }

    /**
     * 언어 설정만 업데이트
     * - 다른 설정은 유지하면서 언어 코드만 변경
     *
     * @param languageCode 변경할 언어 코드
     */
    override suspend fun updateLanguage(languageCode: String) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(languageCode = languageCode)
        saveUserPreferences(newPrefs)
    }

    /**
     * 배경 음악 설정만 업데이트
     * - 다른 설정은 유지하면서 음악 설정만 변경
     *
     * @param isMusicOn 배경 음악 켜기/끄기 상태
     */
    override suspend fun updateMusicSetting(isMusicOn: Boolean) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(isMusicOn = isMusicOn)
        saveUserPreferences(newPrefs)
    }

    /**
     * SharedPreferences에서 설정 로드
     * - 저장된 설정이 없는 경우 기본값 사용
     *
     * @return 로드된 사용자 설정 객체
     */
    private fun loadFromPreferences(): UserPreference {
        return UserPreference(
            languageCode = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE,
            isMusicOn = prefs.getBoolean(KEY_MUSIC_ON, false)
        )
    }

    companion object {
        // SharedPreferences 파일명 및 키 상수
        private const val PREFERENCES_NAME = "tetum_dreams_preferences"
        private const val KEY_LANGUAGE = "language_code"
        private const val DEFAULT_LANGUAGE = "en-ph"
        private const val KEY_MUSIC_ON = "music_on"
    }
}