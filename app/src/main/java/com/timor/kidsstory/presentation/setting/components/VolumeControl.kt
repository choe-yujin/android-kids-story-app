package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun VolumeControlButtonsOnly(
    volume: Float,
    enabled: Boolean,
    onVolumeChange: (Float) -> Unit
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val buttonSize = (24 * scaleFactor).dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // 마이너스 버튼
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                    .clickable(enabled = enabled) {
                        val newVolume = (volume - 0.1f).coerceAtLeast(0f)
                        onVolumeChange(newVolume)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    style = ResponsiveTextUtils.getSettingTextStyle().copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (enabled) AppColors.primary800 else AppColors.neutral400
                )
            }

            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

            Text(
                text = "${(volume * 100).toInt()}%",
                style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                color = if (enabled) AppColors.neutral700 else AppColors.neutral400,
                modifier = Modifier.width((32 * scaleFactor).dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

            // 플러스 버튼
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .clip(CircleShape)
                    .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                    .clickable(enabled = enabled) {
                        val newVolume = (volume + 0.1f).coerceAtMost(1f)
                        onVolumeChange(newVolume)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = ResponsiveTextUtils.getSettingTextStyle().copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (enabled) AppColors.primary800 else AppColors.neutral400
                )
            }
        }
    }
}

@Composable
fun SmallVolumeControlButtonsOnly(
    volume: Float,
    enabled: Boolean,
    onVolumeChange: (Float) -> Unit
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val buttonSize = (20 * scaleFactor).dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // 마이너스 버튼
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                .clickable(enabled = enabled) {
                    val newVolume = (volume - 0.1f).coerceAtLeast(0f)
                    onVolumeChange(newVolume)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = ResponsiveTextUtils.getSettingTextStyle().copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = if (enabled) AppColors.primary800 else AppColors.neutral400
            )
        }

        Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

        Text(
            text = "${(volume * 100).toInt()}%",
            style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
            color = if (enabled) AppColors.neutral700 else AppColors.neutral400,
            modifier = Modifier.width((28 * scaleFactor).dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width((8 * scaleFactor).dp))

        // 플러스 버튼
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                .clickable(enabled = enabled) {
                    val newVolume = (volume + 0.1f).coerceAtMost(1f)
                    onVolumeChange(newVolume)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = ResponsiveTextUtils.getSettingTextStyle().copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = if (enabled) AppColors.primary800 else AppColors.neutral400
            )
        }
    }
}

@Composable
fun VerySmallVolumeControlButtonsOnly(
    volume: Float,
    enabled: Boolean,
    onVolumeChange: (Float) -> Unit
) {
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()
    val buttonSize = (18 * scaleFactor).dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // 마이너스 버튼
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                .clickable(enabled = enabled) {
                    val newVolume = (volume - 0.1f).coerceAtLeast(0f)
                    onVolumeChange(newVolume)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = ResponsiveTextUtils.getSettingTextStyle().copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = if (enabled) AppColors.primary800 else AppColors.neutral400
            )
        }

        Spacer(modifier = Modifier.width((6 * scaleFactor).dp))

        Text(
            text = "${(volume * 100).toInt()}%",
            style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
            color = if (enabled) AppColors.neutral700 else AppColors.neutral400,
            modifier = Modifier.width((24 * scaleFactor).dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width((6 * scaleFactor).dp))

        // 플러스 버튼
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(if (enabled) AppColors.primary300 else AppColors.neutral200)
                .clickable(enabled = enabled) {
                    val newVolume = (volume + 0.1f).coerceAtMost(1f)
                    onVolumeChange(newVolume)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = ResponsiveTextUtils.getSettingTextStyle().copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = if (enabled) AppColors.primary800 else AppColors.neutral400
            )
        }
    }
} 