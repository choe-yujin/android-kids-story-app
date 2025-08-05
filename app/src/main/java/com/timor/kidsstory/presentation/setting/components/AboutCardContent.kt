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
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .padding((16 * scaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_about,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

        // 앱 정보 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((8 * scaleFactor).dp))
                .background(AppColors.blue50)
                .padding(horizontal = (12 * scaleFactor).dp, vertical = (8 * scaleFactor).dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                LocalizedText(
                    resId = R.string.app_name,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle(),
                    color = AppColors.blue700
                )

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LocalizedText(
                            resId = R.string.info_version,
                            style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                            color = AppColors.neutral800
                        )
                        Text(
                            text = " 1.0.2",
                            style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                            color = AppColors.neutral800
                        )
                    }
                    Text(
                        text = "25-08-05",
                        style = ResponsiveTextUtils.getSettingVerySmallTextStyle(),
                        color = AppColors.neutral600
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

        // 라이센스 정보
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LocalizedText(
                resId = R.string.info_license,
                style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                color = AppColors.neutral800,
                modifier = Modifier.padding(bottom = (6 * scaleFactor).dp)
            )
            Text(
                text = "These books are licensed under CC BY 4.0 by Enuma, Inc. & The Foundation SeeArt for Book Culture. To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.",
                style = ResponsiveTextUtils.getLicenseTextStyle(),
                color = AppColors.neutral600,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "ⓒ 2019 by Enuma, Inc. & The Foundation SeeArt for Book Culture",
                style = ResponsiveTextUtils.getLicenseTextStyle(),
                color = AppColors.neutral700,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SmallAboutCardContent(scaleFactor: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((12 * scaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_about,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

        // 앱 정보 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((6 * scaleFactor).dp))
                .background(AppColors.blue50)
                .padding(horizontal = (10 * scaleFactor).dp, vertical = (6 * scaleFactor).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LocalizedText(
                    resId = R.string.app_name,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle(),
                    color = AppColors.blue700
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LocalizedText(
                        resId = R.string.info_version,
                        style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                            fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.65f
                        ),
                        color = AppColors.neutral800
                    )
                    Text(
                        text = ": 1.0.2",
                        style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                            fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.65f
                        ),
                        color = AppColors.neutral800
                    )
                }
                Text(
                    text = "25-08-05",
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.65f
                    ),
                    color = AppColors.neutral600
                )
            }
        }

        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))

        // 라이센스 정보
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LocalizedText(
                resId = R.string.info_license,
                style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                color = AppColors.neutral800,
                modifier = Modifier.padding(bottom = (4 * scaleFactor).dp)
            )
            Text(
                text = "These books are licensed under CC BY 4.0 by Enuma, Inc. & The Foundation SeeArt for Book Culture. To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.",
                style = ResponsiveTextUtils.getLicenseTextStyle(),
                color = AppColors.neutral600,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "ⓒ 2019 by Enuma, Inc. & The Foundation SeeArt for Book Culture",
                style = ResponsiveTextUtils.getLicenseTextStyle(),
                color = AppColors.neutral700,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VerySmallAboutCardContent(scaleFactor: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding((8 * scaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_about,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((6 * scaleFactor).dp))

        // 앱 정보 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape((4 * scaleFactor).dp))
                .background(AppColors.blue50)
                .padding(horizontal = (8 * scaleFactor).dp, vertical = (4 * scaleFactor).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LocalizedText(
                    resId = R.string.app_name,
                    style = ResponsiveTextUtils.getSettingSmallTextStyle(),
                    color = AppColors.blue700
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LocalizedText(
                        resId = R.string.info_version,
                        style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                            fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.55f
                        ),
                        color = AppColors.neutral800
                    )
                    Text(
                        text = ": 1.0.2",
                        style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                            fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.55f
                        ),
                        color = AppColors.neutral800
                    )
                }
                Text(
                    text = "25-08-05",
                    style = ResponsiveTextUtils.getSettingVerySmallTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingVerySmallTextStyle().fontSize * 0.55f
                    ),
                    color = AppColors.neutral600
                )
            }
        }

        Spacer(modifier = Modifier.height((6 * scaleFactor).dp))

        // 라이센스 정보
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LocalizedText(
                resId = R.string.info_license,
                style = ResponsiveTextUtils.getSettingCardTitleStyle(),
                color = AppColors.neutral800,
                modifier = Modifier.padding(bottom = (3 * scaleFactor).dp)
            )
            Text(
                text = "These books are licensed under CC BY 4.0 by Enuma, Inc. & The Foundation SeeArt for Book Culture. To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.",
                style = ResponsiveTextUtils.getLicenseTextStyle(),
                color = AppColors.neutral600,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "ⓒ 2019 by Enuma, Inc. & The Foundation SeeArt for Book Culture",
                style = ResponsiveTextUtils.getLicenseTextStyle(),
                color = AppColors.neutral700,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
} 