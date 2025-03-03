package com.timor.kidsstory.presentation.setting

sealed interface SettingAction {
    data class SwitchClick(val isMusicOn: Boolean): SettingAction
    data object BackButtonClick : SettingAction
}