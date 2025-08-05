package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.setting.SettingAction
import com.timor.kidsstory.presentation.setting.SettingUiState

@Composable
fun WideScreenCardLayout(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float,
    screenWidth: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = (screenWidth * 0.08f).dp, // 좌우 8% 여백
                vertical = (16 * scaleFactor).dp
            ),
        horizontalArrangement = Arrangement.spacedBy((20 * scaleFactor).dp)
    ) {
        // 설정 카드
        SettingCard(
            modifier = Modifier.weight(1f).widthIn(max = 280.dp),
            scaleFactor = scaleFactor
        ) {
            SettingCardContent(state, onAction, scaleFactor)
        }

        // 제작자 카드
        SettingCard(
            modifier = Modifier.weight(1f).widthIn(max = 280.dp),
            scaleFactor = scaleFactor
        ) {
            CreatedByCardContent(scaleFactor, onAction)
        }

        // 정보 카드
        SettingCard(
            modifier = Modifier.weight(1f).widthIn(max = 280.dp),
            scaleFactor = scaleFactor
        ) {
            AboutCardContent(scaleFactor)
        }
    }
}

@Composable
fun NormalScreenCardLayout(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 48.dp,
                vertical = (16 * scaleFactor).dp
            ),
        horizontalArrangement = Arrangement.spacedBy((16 * scaleFactor).dp)
    ) {
        // 설정 카드
        SettingCard(
            modifier = Modifier.weight(1f),
            scaleFactor = scaleFactor
        ) {
            SettingCardContent(state, onAction, scaleFactor)
        }

        // 제작자 카드
        SettingCard(
            modifier = Modifier.weight(1f),
            scaleFactor = scaleFactor
        ) {
            CreatedByCardContent(scaleFactor, onAction)
        }

        // 정보 카드
        SettingCard(
            modifier = Modifier.weight(1f),
            scaleFactor = scaleFactor
        ) {
            AboutCardContent(scaleFactor)
        }
    }
}

@Composable
fun SmallScreenCardLayout(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = (16 * scaleFactor).dp,
                vertical = (12 * scaleFactor).dp
            ),
        verticalArrangement = Arrangement.spacedBy((12 * scaleFactor).dp)
    ) {
        // 설정 카드
        SettingCard(
            modifier = Modifier.fillMaxWidth(),
            scaleFactor = scaleFactor
        ) {
            SmallSettingCardContent(state, onAction, scaleFactor)
        }

        // 제작자 카드
        SettingCard(
            modifier = Modifier.fillMaxWidth(),
            scaleFactor = scaleFactor
        ) {
            SmallCreatedByCardContent(scaleFactor, onAction)
        }

        // 정보 카드
        SettingCard(
            modifier = Modifier.fillMaxWidth(),
            scaleFactor = scaleFactor
        ) {
            SmallAboutCardContent(scaleFactor)
        }
    }
}

@Composable
fun VerySmallScreenCardLayout(
    state: SettingUiState,
    onAction: (SettingAction) -> Unit,
    scaleFactor: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = (8 * scaleFactor).dp,
                vertical = (8 * scaleFactor).dp
            ),
        verticalArrangement = Arrangement.spacedBy((8 * scaleFactor).dp)
    ) {
        // 설정 카드
        SettingCard(
            modifier = Modifier.fillMaxWidth(),
            scaleFactor = scaleFactor
        ) {
            VerySmallSettingCardContent(state, onAction, scaleFactor)
        }

        // 제작자 카드
        SettingCard(
            modifier = Modifier.fillMaxWidth(),
            scaleFactor = scaleFactor
        ) {
            VerySmallCreatedByCardContent(scaleFactor, onAction)
        }

        // 정보 카드
        SettingCard(
            modifier = Modifier.fillMaxWidth(),
            scaleFactor = scaleFactor
        ) {
            VerySmallAboutCardContent(scaleFactor)
        }
    }
} 