package com.timor.kidsstory.presentation.setting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(SettingUiState())
    val state = _state.asStateFlow()
}