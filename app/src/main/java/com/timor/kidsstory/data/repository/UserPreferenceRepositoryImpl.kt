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
        android.util.Log.d("UserPreferenceRepo", "Saving preferences. isFirstRun: ${userPreference.isFirstRun}")
        // SharedPreferences에 저장
        prefs.edit {
            putString(KEY_LANGUAGE, userPreference.languageCode)
            putInt(KEY_SELECTED_LEVEL, userPreference.selectedLevel)
            putBoolean(KEY_IS_FIRST_RUN, userPreference.isFirstRun)
            putBoolean(KEY_HAS_COMPLETED_LEVEL_TEST, userPreference.hasCompletedLevelTest)
            putBoolean(KEY_MUSIC_ON, userPreference.isMusicOn)
            putBoolean(KEY_SOUND_EFFECT_ON, userPreference.isSoundEffectOn)
            putFloat(KEY_MUSIC_VOLUME, userPreference.musicVolume)
            putFloat(KEY_SOUND_EFFECT_VOLUME, userPreference.soundEffectVolume)
        }

        // 메모리 캐시 업데이트
        _userPreferencesFlow.value = userPreference
        android.util.Log.d("UserPreferenceRepo", "Preferences saved and flow updated.")
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
     * 배경음악 음량만 업데이트
     * - 다른 설정은 유지하면서 배경음악 음량만 변경
     *
     * @param volume 배경음악 음량 (0.0 ~ 1.0)
     */
    override suspend fun updateMusicVolume(volume: Float) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(musicVolume = volume)
        saveUserPreferences(newPrefs)
    }

    /**
     * 효과음 설정만 업데이트
     * - 다른 설정은 유지하면서 효과음 설정만 변경
     *
     * @param isSoundEffectOn 효과음 켜기/끄기 상태
     */
    override suspend fun updateSoundEffectSetting(isSoundEffectOn: Boolean) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(isSoundEffectOn = isSoundEffectOn)
        saveUserPreferences(newPrefs)
    }

    /**
     * 효과음 음량만 업데이트
     * - 다른 설정은 유지하면서 효과음 음량만 변경
     *
     * @param volume 효과음 음량 (0.0 ~ 1.0)
     */
    override suspend fun updateSoundEffectVolume(volume: Float) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(soundEffectVolume = volume)
        saveUserPreferences(newPrefs)
    }
    
    /**
     * 첫 실행 상태 업데이트
     * - 첫 실행 완료 처리
     */
    override suspend fun markFirstRunComplete() {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(isFirstRun = false)
        saveUserPreferences(newPrefs)
    }
    
    /**
     * 선택된 언어와 레벨 업데이트
     * - 레벨 테스트 완료 후 언어와 레벨을 함께 저장
     *
     * @param languageCode 선택된 언어 코드
     * @param level 측정된 또는 선택된 레벨
     * @param hasCompletedLevelTest 레벨 테스트 완료 여부 (파라미터명 수정)
     */
    override suspend fun updateLanguageAndLevel(
        languageCode: String,
        level: Int,
        hasCompletedLevelTest: Boolean
    ) {
        val currentPrefs = _userPreferencesFlow.value
        val newPrefs = currentPrefs.copy(
            languageCode = languageCode,
            selectedLevel = level,
            hasCompletedLevelTest = hasCompletedLevelTest,
            isFirstRun = false // 언어/레벨 설정 시 첫 실행 완료
        )
        android.util.Log.d("UserPreferenceRepo", "Updating language and level. isFirstRun will be: ${newPrefs.isFirstRun}")
        saveUserPreferences(newPrefs)
    }

    /**
     * SharedPreferences에서 설정 로드
     * - 저장된 설정이 없는 경우 기본값 사용
     * - 앱 최초 실행 시 기본값들을 SharedPreferences에 저장하여 동기화 보장
     *
     * @return 로드된 사용자 설정 객체
     */
    private fun loadFromPreferences(): UserPreference {
        // 기본값으로 UserPreference 객체 생성 (UserPreference.kt의 기본값과 완전 일치)
        val defaultPreference = UserPreference(
            languageCode = "", // 미선택 상태
            selectedLevel = 3,
            isFirstRun = true,
            hasCompletedLevelTest = false,
            isMusicOn = true,  // 음악 기본값: 켜짐 (UserPreference.kt 기본값과 일치)
            isSoundEffectOn = true,  // 효과음 기본값: 켜짐 (UserPreference.kt와 일치)
            musicVolume = 0.7f,  // UserPreference.kt 기본값과 일치
            soundEffectVolume = 0.7f  // UserPreference.kt 기본값과 일치
        )
        
        // 기존 설정이 있는 경우 로드
        val loadedPreference = UserPreference(
            languageCode = prefs.getString(KEY_LANGUAGE, defaultPreference.languageCode) ?: defaultPreference.languageCode,
            selectedLevel = prefs.getInt(KEY_SELECTED_LEVEL, defaultPreference.selectedLevel),
            isFirstRun = prefs.getBoolean(KEY_IS_FIRST_RUN, defaultPreference.isFirstRun),
            hasCompletedLevelTest = prefs.getBoolean(KEY_HAS_COMPLETED_LEVEL_TEST, defaultPreference.hasCompletedLevelTest),
            isMusicOn = prefs.getBoolean(KEY_MUSIC_ON, defaultPreference.isMusicOn),
            isSoundEffectOn = prefs.getBoolean(KEY_SOUND_EFFECT_ON, defaultPreference.isSoundEffectOn),
            musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, defaultPreference.musicVolume),
            soundEffectVolume = prefs.getFloat(KEY_SOUND_EFFECT_VOLUME, defaultPreference.soundEffectVolume)
        )
        
        android.util.Log.d("UserPreferenceRepository", "📁 Loaded existing preferences - isFirstRun=${loadedPreference.isFirstRun}")
        return loadedPreference
    }

    companion object {
        // SharedPreferences 파일명 및 키 상수
        private const val PREFERENCES_NAME = "tetum_dreams_preferences"
        private const val KEY_LANGUAGE = "language_code"
        private const val KEY_SELECTED_LEVEL = "selected_level"
        private const val KEY_IS_FIRST_RUN = "is_first_run"
        private const val KEY_HAS_COMPLETED_LEVEL_TEST = "has_completed_level_test"
        private const val KEY_MUSIC_ON = "music_on"
        private const val KEY_SOUND_EFFECT_ON = "sound_effect_on"
        private const val KEY_MUSIC_VOLUME = "music_volume"
        private const val KEY_SOUND_EFFECT_VOLUME = "sound_effect_volume"
    }
}