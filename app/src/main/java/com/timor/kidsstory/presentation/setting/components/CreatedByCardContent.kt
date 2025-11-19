package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.setting.SettingAction
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun CreatedByCardContent(scaleFactor: Float, onAction: (SettingAction) -> Unit, isTablet: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding((8 * scaleFactor).dp)
            .verticalScroll(rememberScrollState()), // Add vertical scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_created_by,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height((12 * scaleFactor).dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(if (isTablet) (12 * scaleFactor).dp else (6 * scaleFactor).dp)
        ) {
            DeveloperRow(
                roleResId = R.string.info_dev_yujin,
                flagResId = R.drawable.flag_ko,
                hasEmail = true,
                onEmailClick = { onAction(SettingAction.EmailIconClick) }
            )
            DeveloperRow(
                roleResId = R.string.info_dev_jaeyeon,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_des_jinsung,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_edu_yuni,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_edu_jiyoung,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_cm_jisu,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_tl_rosalina,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_tl_helia,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_tl_lourenco,
                flagResId = R.drawable.flag_tet,
hasEmail = false,
                onEmailClick = {}
            )
            DeveloperRow(
                roleResId = R.string.info_tl_alexandrino,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
        }
    }
}

@Composable
fun SmallCreatedByCardContent(scaleFactor: Float, onAction: (SettingAction) -> Unit, isTablet: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding((12 * scaleFactor).dp)
            .verticalScroll(rememberScrollState()), // Add vertical scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_created_by,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height(if (isTablet) (16 * scaleFactor).dp else (8 * scaleFactor).dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy((4 * scaleFactor).dp)
        ) {
            SmallDeveloperRow(
                roleResId = R.string.info_dev_yujin,
                flagResId = R.drawable.flag_ko,
                hasEmail = true,
                onEmailClick = { onAction(SettingAction.EmailIconClick) }
            )
            SmallDeveloperRow(
                roleResId = R.string.info_dev_jaeyeon,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_des_jinsung,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_edu_yuni,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_edu_jiyoung,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_cm_jisu,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_tl_rosalina,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_tl_helia,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_tl_lourenco,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            SmallDeveloperRow(
                roleResId = R.string.info_tl_alexandrino,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
        }
    }
}

@Composable
fun VerySmallCreatedByCardContent(scaleFactor: Float, onAction: (SettingAction) -> Unit, isTablet: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding((8 * scaleFactor).dp)
            .verticalScroll(rememberScrollState()), // Add vertical scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            resId = R.string.info_created_by,
            style = ResponsiveTextUtils.getSettingCardTitleStyle(),
            color = AppColors.neutral800
        )

        Spacer(modifier = Modifier.height(if (isTablet) (12 * scaleFactor).dp else (6 * scaleFactor).dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy((3 * scaleFactor).dp)
        ) {
            VerySmallDeveloperRow(
                roleResId = R.string.info_dev_yujin,
                flagResId = R.drawable.flag_ko,
                hasEmail = true,
                onEmailClick = { onAction(SettingAction.EmailIconClick) }
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_dev_jaeyeon,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_des_jinsung,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_edu_yuni,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_edu_jiyoung,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_cm_jisu,
                flagResId = R.drawable.flag_ko,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_tl_rosalina,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_tl_helia,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_tl_lourenco,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
            VerySmallDeveloperRow(
                roleResId = R.string.info_tl_alexandrino,
                flagResId = R.drawable.flag_tet,
                hasEmail = false,
                onEmailClick = {}
            )
        }
    }
}

@Composable
fun DeveloperRow(
    roleResId: Int,
    flagResId: Int,
    hasEmail: Boolean = false,
    onEmailClick: () -> Unit = {}
) {
    val scaleFactor = ResponsiveTextUtils.getSettingScaleFactor()
    val iconSize = (16 * scaleFactor).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape((8 * scaleFactor).dp))
            .background(AppColors.neutral100)
            .padding(
                horizontal = (12 * scaleFactor).dp,
                vertical = (6 * scaleFactor).dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LocalizedText(
                    resId = roleResId,
                    style = ResponsiveTextUtils.getDeveloperNameTextStyle(),
                    color = AppColors.neutral800,
                )
            }

            if (hasEmail) {
                Card(
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onEmailClick() },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = AppColors.primary100),
                    border = BorderStroke(
                        width = 1.dp,
                        color = AppColors.primary300
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = "Email",
                            modifier = Modifier.size((iconSize.value * 0.6f).dp),
                            tint = AppColors.primary600
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun SmallDeveloperRow(
    roleResId: Int,
    flagResId: Int,
    hasEmail: Boolean = false,
    onEmailClick: () -> Unit = {}
) {
    val scaleFactor = ResponsiveTextUtils.getSettingScaleFactor()
    val iconSize = (14 * scaleFactor).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape((6 * scaleFactor).dp))
            .background(AppColors.neutral100)
            .padding(
                horizontal = (8 * scaleFactor).dp,
                vertical = (3 * scaleFactor).dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LocalizedText(
                    resId = roleResId,
                    style = ResponsiveTextUtils.getDeveloperNameTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getDeveloperNameTextStyle().fontSize * 0.75f
                    ),
                    color = AppColors.neutral800,
                )
            }

            if (hasEmail) {
                Card(
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onEmailClick() },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = AppColors.primary100),
                    border = BorderStroke(
                        width = 1.dp,
                        color = AppColors.primary300
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = "Email",
                            modifier = Modifier.size((iconSize.value * 0.6f).dp),
                            tint = AppColors.primary600
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))
            }

            Icon(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun VerySmallDeveloperRow(
    roleResId: Int,
    flagResId: Int,
    hasEmail: Boolean = false,
    onEmailClick: () -> Unit = {}
) {
    val scaleFactor = ResponsiveTextUtils.getSettingScaleFactor()
    val iconSize = (12 * scaleFactor).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape((4 * scaleFactor).dp))
            .background(AppColors.neutral100)
            .padding(
                horizontal = (6 * scaleFactor).dp,
                vertical = (2 * scaleFactor).dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LocalizedText(
                    resId = roleResId,
                    style = ResponsiveTextUtils.getDeveloperNameTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getDeveloperNameTextStyle().fontSize * 0.65f
                    ),
                    color = AppColors.neutral800,
                )
            }

            if (hasEmail) {
                Card(
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onEmailClick() },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = AppColors.primary100),
                    border = BorderStroke(
                        width = 1.dp,
                        color = AppColors.primary300
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = "Email",
                            modifier = Modifier.size((iconSize.value * 0.6f).dp),
                            tint = AppColors.primary600
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))
            }

            Icon(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.Unspecified
            )
        }
    }
} 