package com.timor.kidsstory.presentation.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.util.ContextLanguageHelper
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

@Composable
fun CustomToggle(
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    selectedLanguageCode: String = "en" // 추가: 선택된 언어 코드
) {
    val context = LocalContext.current
    val responsivePadding = (5 * ResponsiveTextUtils.getScreenScaleFactor()).dp
    val responsiveInnerPadding = (3 * ResponsiveTextUtils.getScreenScaleFactor()).dp
    val toggleSize = (16 * ResponsiveTextUtils.getScreenScaleFactor()).dp

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isChecked) AppColors.primary300 else Color.LightGray)
            .clickable { onToggle(!isChecked) }
            .padding(horizontal = responsivePadding, vertical = responsiveInnerPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isChecked) {
                Text(
                    text = ContextLanguageHelper.getStringInLanguage(
                        context, selectedLanguageCode, R.string.info_on
                    ),
                    style = ResponsiveTextUtils.getSettingToggleTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingToggleTextStyle().fontSize * 0.8f
                    ),
                    color = AppColors.primary800,
                    modifier = Modifier.padding(horizontal = responsiveInnerPadding)
                )
            }
            Box(
                modifier = Modifier
                    .size(toggleSize)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            if (!isChecked) {
                Text(
                    text = ContextLanguageHelper.getStringInLanguage(
                        context, selectedLanguageCode, R.string.info_off
                    ),
                    style = ResponsiveTextUtils.getSettingToggleTextStyle().copy(
                        fontSize = ResponsiveTextUtils.getSettingToggleTextStyle().fontSize * 0.8f
                    ),
                    color = AppColors.neutral400,
                    modifier = Modifier.padding(horizontal = responsiveInnerPadding)
                )
            }
        }
    }
}
