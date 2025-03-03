package com.timor.kidsstory.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val musicSettingUseCase: MusicSettingUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                _state.update { it.copy(isMusicOn = isMusicOn) }
            }
        }
    }

    // 스위치 조절
    private fun toggleMusicSetting(isMusicOn: Boolean) {
        viewModelScope.launch {
            musicSettingUseCase.toggleMusicSetting(isMusicOn)
        }
    }

    fun onAction(action: SettingAction) {
        when (action) {
            is SettingAction.SwitchClick -> toggleMusicSetting(action.isMusicOn)
            is SettingAction.BackButtonClick -> {}
        }
    }
}