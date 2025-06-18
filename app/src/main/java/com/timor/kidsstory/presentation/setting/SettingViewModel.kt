package com.timor.kidsstory.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import com.timor.kidsstory.domain.util.SoundEffectManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val musicSettingUseCase: MusicSettingUseCase,
    private val soundEffectManager: SoundEffectManager,
) : ViewModel() {
    // UI 상태 관리
    private val _state = MutableStateFlow(SettingUiState())
    val state = _state.asStateFlow()

    /**
     * 초기화 - 사용자 설정 로드
     */
    init {
        // 배경 음악 설정 로드 및 관찰
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                _state.update { it.copy(isMusicOn = isMusicOn) }
            }
        }
    }

    /**
     * 뷰모델 종료 시 리소스 해제
     */
    override fun onCleared() {
        super.onCleared()
        soundEffectManager.release()
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
     * UI 액션 처리
     * - 사용자 인터랙션에 따른 상태 변경 및 비즈니스 로직 실행
     *
     * @param action 처리할 액션
     */
    fun onAction(action: SettingAction) {
        when (action) {
            is SettingAction.SwitchClick -> {
                soundEffectManager.playButtonClick()
                toggleMusicSetting(action.isMusicOn)
            }
            is SettingAction.BackButtonClick -> {
                soundEffectManager.playButtonClick()
            }  // 상위 컴포넌트에서 처리
        }
    }
}
