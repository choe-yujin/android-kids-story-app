package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.setting.SettingAction
import com.timor.kidsstory.presentation.setting.SettingUiState
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun SettingCardContent(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((16 * scaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_setting,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

        // 음악 설정
        SettingToggleRow(
            titleResId = R.string.info_music,
            isChecked = state.isMusicOn,
            onToggle = { onAction(SettingAction.MusicSwitchClick(it)) }
        )

        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

        VolumeControlButtonsOnly(
            volume = state.musicVolume,
            enabled = state.isMusicOn,
            onVolumeChange = { onAction(SettingAction.MusicVolumeChange(it)) }
        )

        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

        // 효과음 설정
        SettingToggleRow(
            titleResId = R.string.info_sound_effect,
            isChecked = state.isSoundEffectOn,
            onToggle = { onAction(SettingAction.SoundEffectSwitchClick(it)) }
        )

        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

        VolumeControlButtonsOnly(
            volume = state.soundEffectVolume,
            enabled = state.isSoundEffectOn,
            onVolumeChange = { onAction(SettingAction.SoundEffectVolumeChange(it)) }
        )
    }
}

@Composable
fun SmallSettingCardContent(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((12 * scaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_setting,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

        // 음악 설정
        SmallSettingToggleRow(
            titleResId = R.string.info_music,
            isChecked = state.isMusicOn,
            onToggle = { onAction(SettingAction.MusicSwitchClick(it)) }
        )

        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

        SmallVolumeControlButtonsOnly(
            volume = state.musicVolume,
            enabled = state.isMusicOn,
            onVolumeChange = { onAction(SettingAction.MusicVolumeChange(it)) }
        )

        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

        // 효과음 설정
        SmallSettingToggleRow(
            titleResId = R.string.info_sound_effect,
            isChecked = state.isSoundEffectOn,
            onToggle = { onAction(SettingAction.SoundEffectSwitchClick(it)) }
        )

        Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

        SmallVolumeControlButtonsOnly(
            volume = state.soundEffectVolume,
            enabled = state.isSoundEffectOn,
            onVolumeChange = { onAction(SettingAction.SoundEffectVolumeChange(it)) }
        )
    }
}

@Composable
fun VerySmallSettingCardContent(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((8 * scaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_setting,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((6 * scaleFactor).dp))

        // 음악 설정
        VerySmallSettingToggleRow(
            titleResId = R.string.info_music,
            isChecked = state.isMusicOn,
            onToggle = { onAction(SettingAction.MusicSwitchClick(it)) }
        )

        Spacer(modifier = Modifier.height((3 * scaleFactor).dp))

        VerySmallVolumeControlButtonsOnly(
            volume = state.musicVolume,
            enabled = state.isMusicOn,
            onVolumeChange = { onAction(SettingAction.MusicVolumeChange(it)) }
        )

        Spacer(modifier = Modifier.height((6 * scaleFactor).dp))

        // 효과음 설정
        VerySmallSettingToggleRow(
            titleResId = R.string.info_sound_effect,
            isChecked = state.isSoundEffectOn,
            onToggle = { onAction(SettingAction.SoundEffectSwitchClick(it)) }
        )

        Spacer(modifier = Modifier.height((3 * scaleFactor).dp))

        VerySmallVolumeControlButtonsOnly(
            volume = state.soundEffectVolume,
            enabled = state.isSoundEffectOn,
            onVolumeChange = { onAction(SettingAction.SoundEffectVolumeChange(it)) }
        )
    }
}

@Composable
fun SettingToggleRow(
    titleResId: Int,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocalizedText(
            resId = titleResId,
            style = ResponsiveTextUtils.getSettingTextStyle(),
            color = AppColors.neutral700
        )

        Spacer(modifier = Modifier.weight(1f))

        CustomToggle(
            isChecked = isChecked,
            onToggle = onToggle
        )
    }
}

@Composable
fun SmallSettingToggleRow(
    titleResId: Int,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocalizedText(
            resId = titleResId,
            style = ResponsiveTextUtils.getSettingTextStyle().copy(
                fontSize = ResponsiveTextUtils.getSettingTextStyle().fontSize * 0.75f
            ),
            color = AppColors.neutral700,
            modifier = Modifier.weight(1f)
        )

        CustomToggle(
            isChecked = isChecked,
            onToggle = onToggle
        )
    }
}

@Composable
fun VerySmallSettingToggleRow(
    titleResId: Int,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocalizedText(
            resId = titleResId,
            style = ResponsiveTextUtils.getSettingTextStyle().copy(
                fontSize = ResponsiveTextUtils.getSettingTextStyle().fontSize * 0.65f
            ),
            color = AppColors.neutral700,
            modifier = Modifier.weight(1f)
        )

        CustomToggle(
            isChecked = isChecked,
            onToggle = onToggle
        )
    }
} 