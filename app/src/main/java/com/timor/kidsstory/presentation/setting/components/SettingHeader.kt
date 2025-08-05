package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.setting.SettingAction
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun SettingHeader(onAction: (SettingAction) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth < 400
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ResponsiveTextUtils.getHeaderHeight().dp)
            .background(AppColors.primary300)
    ) {
        // 닫기 버튼
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Close",
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = if (isSmallScreen) 16.dp else 48.dp)
                .size(ResponsiveTextUtils.getHeaderIconSize().dp)
                .clickable { onAction(SettingAction.BackButtonClick) }
        )

        // 앱 로고
        Icon(
            painter = painterResource(id = R.drawable.info_logo_test),
            contentDescription = "Info",
            modifier = Modifier
                .align(Alignment.Center)
                .size((48 * ResponsiveTextUtils.getScreenScaleFactor()).dp),
            tint = Color.Unspecified
        )
    }
} 