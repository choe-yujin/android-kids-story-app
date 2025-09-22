package com.timor.kidsstory.presentation.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.util.LanguageConstants
import com.timor.kidsstory.domain.util.SoundEffectManager
import com.timor.kidsstory.domain.manager.FirstRunManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 설정 화면의 상태 관리 및 비즈니스 로직 처리 뷰모델
 * - 설정 값 관리 및 저장
 * - UI 상태 업데이트
 *
 * @property musicSettingUseCase 음악 설정 관련 유스케이스
 * @property soundEffectManager 효과음 관리자
 * @property context 애플리케이션 컨텍스트
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val musicSettingUseCase: MusicSettingUseCase,
    private val soundEffectManager: SoundEffectManager,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val getUserPreferenceUseCase: GetUserPreferenceUseCase,
    private val firstRunManager: FirstRunManager,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    // UI 상태 관리
    private val _state = MutableStateFlow(SettingUiState())
    val state = _state.asStateFlow()

    /**
     * 초기화 - 사용자 설정 로드 및 동기화
     */
    init {
        // 배경 음악 설정 로드 및 관찰
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                _state.update { it.copy(isMusicOn = isMusicOn) }
            }
        }
        
        // 배경 음악 음량 설정 로드 및 관찰 (음량만 관리, 재생은 BookshelfViewModel에서)
        viewModelScope.launch {
            musicSettingUseCase.musicVolume.collect { musicVolume ->
                _state.update { it.copy(musicVolume = musicVolume) }
                // 음량은 MusicManager에 직접 설정하지 않고, UseCase를 통해서만 관리
            }
        }
        
        // 🔧 사용자 설정에서 효과음 설정 및 언어 설정 로드
        viewModelScope.launch {
            getUserPreferenceUseCase().collect { preference ->
                // 언어 설정 로드
                val languageCode = if (preference.languageCode.isBlank()) {
                    LanguageConstants.DEFAULT_LANGUAGE.code
                } else {
                    preference.languageCode
                }

                val language = LanguageConstants.SUPPORTED_LANGUAGES.find {
                    it.code == languageCode
                } ?: LanguageConstants.DEFAULT_LANGUAGE

                // UI 상태 업데이트
                _state.update { 
                    it.copy(
                        isSoundEffectOn = preference.isSoundEffectOn,
                        soundEffectVolume = preference.soundEffectVolume,
                        currentLanguage = language // 현재 언어 추가
                    ) 
                }
                
                // ⚡ 만약 설정이 변경되었다면 SoundEffectManager에 적용
                if (soundEffectManager.isEnabled() != preference.isSoundEffectOn) {
                    soundEffectManager.setEnabled(preference.isSoundEffectOn)
                }
                if (soundEffectManager.getVolume() != preference.soundEffectVolume) {
                    soundEffectManager.setVolume(preference.soundEffectVolume)
                }
                
                // 디버깅을 위한 로그
                android.util.Log.d(
                    "SettingViewModel", 
                    "🔄 Preference loaded: enabled=${preference.isSoundEffectOn}, volume=${preference.soundEffectVolume}, language=${language.code}"
                )
            }
        }
    }

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        soundEffectManager.release()
        // MusicManager는 BookshelfViewModel에서만 관리
    }

    /**
     * 배경 음악 설정 토글
     * - 음악 켜기/끄기 상태 변경 및 저장
     *
     * @param isMusicOn 변경할 음악 상태
     */
    private fun toggleMusicSetting(isMusicOn: Boolean) {
        viewModelScope.launch {
            musicSettingUseCase.toggleMusicSetting(isMusicOn)
        }
    }

    /**
     * 효과음 설정 토글
     * - 효과음 켜기/끄기 상태 변경
     *
     * @param isSoundEffectOn 변경할 효과음 상태
     */
    private fun toggleSoundEffectSetting(isSoundEffectOn: Boolean) {
        viewModelScope.launch {
            userPreferenceRepository.updateSoundEffectSetting(isSoundEffectOn)
        }
    }

    /**
     * 배경음악 음량 조절
     * - 배경음악 음량 설정 변경 및 저장
     *
     * @param volume 변경할 음량 (0.0 ~ 1.0)
     */
    private fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            musicSettingUseCase.setMusicVolume(volume)
            // 음량 조절만 하고, 음악 재생/정지는 BookshelfViewModel에서 처리
        }
    }

    /**
     * 효과음 음량 조절
     * - 효과음 음량 설정 변경 및 저장
     *
     * @param volume 변경할 음량 (0.0 ~ 1.0)
     */
    private fun setSoundEffectVolume(volume: Float) {
        viewModelScope.launch {
            userPreferenceRepository.updateSoundEffectVolume(volume)
        }
    }

    /**
     * 이메일 다이얼로그 표시
     */
    private fun showEmailDialog() {
        _state.update { it.copy(showEmailDialog = true) }
    }

    /**
     * 이메일 다이얼로그 숨김
     */
    private fun dismissEmailDialog() {
        _state.update { it.copy(showEmailDialog = false) }
    }

    /**
     * 웹사이트 브라우저에서 열기
     */
    private fun openWebsite() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://taletail.shop"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            // 브라우저가 없거나 다른 문제가 발생한 경우 처리
            // 일반적으로는 로그만 남기고 처리
        }
    }
    
    /**
     * 🔧 디버깅용: 앱 데이터 초기화 (문제 해결 후 제거 예정)
     */
    private fun resetAppData() {
        viewModelScope.launch {
            try {
                android.util.Log.w("SettingViewModel", "🔄 사용자 요청으로 앱 데이터 초기화 시작")
                firstRunManager.resetAllPreferences()
                android.util.Log.w("SettingViewModel", "✅ 앱 데이터 초기화 완료")
                
                // UI 상태 업데이트 알림
                _state.update { it.copy(showResetConfirmation = true) }
            } catch (e: Exception) {
                android.util.Log.e("SettingViewModel", "❌ 앱 데이터 초기화 실패", e)
            }
        }
    }
    
    /**
     * 초기화 확인 다이얼로그 닫기
     */
    private fun dismissResetConfirmation() {
        _state.update { it.copy(showResetConfirmation = false) }
    }

    /**
     * UI 액션 처리
     * - 사용자 인터랙션에 따른 상태 변경 및 비즈니스 로직 실행
     *
     * @param action 처리할 액션
     */
    fun onAction(action: SettingAction) {
        when (action) {
            is SettingAction.MusicSwitchClick -> {
                soundEffectManager.playButtonClick()
                toggleMusicSetting(action.isMusicOn)
            }
            is SettingAction.SoundEffectSwitchClick -> {
                // 효과음이 켜져있을 때만 클릭 사운드 재생
                if (_state.value.isSoundEffectOn) {
                    soundEffectManager.playButtonClick()
                }
                toggleSoundEffectSetting(action.isSoundEffectOn)
            }
            is SettingAction.EmailIconClick -> {
                soundEffectManager.playButtonClick()
                showEmailDialog()
            }
            is SettingAction.WebsiteLinkClick -> {
                soundEffectManager.playButtonClick()
                openWebsite()
                dismissEmailDialog()  // 웹사이트 연 후 다이얼로그 닫기
            }
            is SettingAction.DismissEmailDialog -> {
                dismissEmailDialog()
            }
            is SettingAction.BackButtonClick -> {
                soundEffectManager.playButtonClick()
            }  // 상위 컴포넌트에서 처리
            is SettingAction.MusicVolumeChange -> {
                soundEffectManager.playButtonClick()
                setMusicVolume(action.volume)
            }
            is SettingAction.SoundEffectVolumeChange -> {
                // 효과음이 켜져있을 때만 클릭 사운드 재생
                if (_state.value.isSoundEffectOn) {
                    soundEffectManager.playButtonClick()
                }
                setSoundEffectVolume(action.volume)
            }
            is SettingAction.ResetAppData -> {
                soundEffectManager.playButtonClick()
                resetAppData()
            }
            is SettingAction.DismissResetConfirmation -> {
                dismissResetConfirmation()
            }
        }
    }
}
