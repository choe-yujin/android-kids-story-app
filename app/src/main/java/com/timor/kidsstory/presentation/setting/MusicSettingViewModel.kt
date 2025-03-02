package com.timor.kidsstory.presentation.setting

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.usecase.MusicSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSettingViewModel @Inject constructor(
    private val musicSettingUseCase: MusicSettingUseCase,
    private val mediaPlayer: MediaPlayer,
) : ViewModel() {

    private val _isMusicOn = MutableStateFlow(false)
    val isMusicOn = _isMusicOn.asStateFlow()

    init {
        viewModelScope.launch {
            musicSettingUseCase.isMusicOn.collect { isMusicOn ->
                _isMusicOn.value = isMusicOn
                if (isMusicOn) {
                    playMusic()
                } else {
                    stopMusic()
                }
            }
        }
    }

    // 스위치 조절
    fun toggleMusicSetting() {
        viewModelScope.launch {
            musicSettingUseCase.toggleMusicSetting(_isMusicOn.value)
        }
    }


    private fun playMusic() {
        if (!_isMusicOn.value) return
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }


    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }


    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()       // viewModel 파괴시 mediaPlayer 리소스 해제
    }
}