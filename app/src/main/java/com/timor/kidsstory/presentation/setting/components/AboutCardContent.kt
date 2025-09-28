package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun AboutCardContent(scaleFactor: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding((8 * scaleFactor).dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_about,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

        // 프로젝트 소개 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((8 * scaleFactor).dp))
                .background(AppColors.blue50)
                .padding((12 * scaleFactor).dp)
        ) {
            Column {
                LocalizedText(
                    resId = R.string.side_project_title,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle(),
                    color = AppColors.blue700
                )
                
                Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.side_project_description,
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height((12 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.collaboration_title,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle(),
                    color = AppColors.blue700
                )
                
                Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.collaboration_description,
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun SmallAboutCardContent(scaleFactor: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((12 * scaleFactor).dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_about,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

        // 프로젝트 소개 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((6 * scaleFactor).dp))
                .background(AppColors.blue50)
                .padding((10 * scaleFactor).dp)
        ) {
            Column {
                LocalizedText(
                    resId = R.string.side_project_title,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingSmallTextStyle().fontSize * 0.9f
                    ),
                    color = AppColors.blue700
                )
                
                Spacer(modifier = Modifier.height((6 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.side_project_description,
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.75f
                    ),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.collaboration_title,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingSmallTextStyle().fontSize * 0.9f
                    ),
                    color = AppColors.blue700
                )
                
                Spacer(modifier = Modifier.height((6 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.collaboration_description,
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.75f
                    ),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun VerySmallAboutCardContent(scaleFactor: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((8 * scaleFactor).dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_about,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((6 * scaleFactor).dp))

        // 프로젝트 소개 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((4 * scaleFactor).dp))
                .background(AppColors.blue50)
                .padding((8 * scaleFactor).dp)
        ) {
            Column {
                LocalizedText(
                    resId = R.string.side_project_title,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingSmallTextStyle().fontSize * 0.8f
                    ),
                    color = AppColors.blue700
                )
                
                Spacer(modifier = Modifier.height((4 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.side_project_description,
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.65f
                    ),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height((6 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.collaboration_title,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingSmallTextStyle().fontSize * 0.8f
                    ),
                    color = AppColors.blue700
                )
                
                Spacer(modifier = Modifier.height((4 * scaleFactor).dp))
                
                LocalizedText(
                    resId = R.string.collaboration_description,
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.65f
                    ),
                    color = AppColors.neutral700,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
} 