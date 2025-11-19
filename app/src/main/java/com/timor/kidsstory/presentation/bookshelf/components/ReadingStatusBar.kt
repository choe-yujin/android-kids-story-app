package com.timor.kidsstory.presentation.bookshelf.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.util.LanguageManager
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils
import java.util.Locale

/**
 * 읽음 상태 필터
 */
enum class ReadingStatusFilter {
    ALL, READING, READ, UNREAD
}

/**
 * 오른쪽 세로 읽음 상태 바 - 내용에 맞춰 동적 크기 조정
 */
@Composable
fun ReadingStatusBar(
    modifier: Modifier = Modifier,
    currentLanguageCode: String = "en",
    hasBooks: Boolean = true, // 현재 필터에 책이 있는지
    selectedStatus: ReadingStatusFilter = ReadingStatusFilter.ALL,
    onStatusSelected: (ReadingStatusFilter) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scaleFactor = ResponsiveTextUtils.getScreenScaleFactor()

    val isTablet = screenWidth >= 800

    val barWidth = if (isTablet) (60 * scaleFactor).dp else (45 * scaleFactor).dp
    val iconSize = if (isTablet) (24 * scaleFactor).dp else (16 * scaleFactor).dp
    val textSize = if (isTablet) (10 * scaleFactor).sp else (8 * scaleFactor).sp
    val itemSpacing = if (isTablet) (0 * scaleFactor).dp else (4 * scaleFactor).dp
    val betweenItemSpacing = if (isTablet) (12 * scaleFactor).dp else (8 * scaleFactor).dp
    val verticalPadding = if (isTablet) (16 * scaleFactor).dp else (12 * scaleFactor).dp

    val textHeight = (textSize.value * 1.2f).dp
    val itemHeight = iconSize + itemSpacing + textHeight
    val totalHeight = verticalPadding * 2 + (itemHeight * 4) + (betweenItemSpacing * 3) + (24 * scaleFactor).dp

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .width(barWidth)
            .height(totalHeight)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(betweenItemSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ReadingStatusItem(
                activeIconRes = R.drawable.ic_all,
                inactiveIconRes = R.drawable.ic_all_none,
                textResId = R.string.reading_status_all,
                isSelected = selectedStatus == ReadingStatusFilter.ALL,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.ALL) }
            )

            ReadingStatusItem(
                activeIconRes = R.drawable.ic_reading,
                inactiveIconRes = R.drawable.ic_reading_none,
                textResId = R.string.reading_status_reading,
                isSelected = selectedStatus == ReadingStatusFilter.READING,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.READING) }
            )

            ReadingStatusItem(
                activeIconRes = R.drawable.ic_read,
                inactiveIconRes = R.drawable.ic_read_none,
                textResId = R.string.reading_status_read,
                isSelected = selectedStatus == ReadingStatusFilter.READ,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.READ) }
            )

            ReadingStatusItem(
                activeIconRes = R.drawable.ic_unread,
                inactiveIconRes = R.drawable.ic_unread_none,
                textResId = R.string.reading_status_unread,
                isSelected = selectedStatus == ReadingStatusFilter.UNREAD,
                isEnabled = hasBooks,
                iconSize = iconSize,
                textSize = textSize,
                itemSpacing = itemSpacing,
                currentLanguageCode = currentLanguageCode,
                onClick = { if (hasBooks) onStatusSelected(ReadingStatusFilter.UNREAD) }
            )
        }
    }
}

@Composable
private fun ReadingStatusItem(
    activeIconRes: Int,
    inactiveIconRes: Int,
    @StringRes textResId: Int,
    isSelected: Boolean,
    isEnabled: Boolean,
    iconSize: androidx.compose.ui.unit.Dp,
    textSize: androidx.compose.ui.unit.TextUnit,
    itemSpacing: androidx.compose.ui.unit.Dp,
    currentLanguageCode: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val text = remember(LanguageManager.getCurrentLanguageCode(), textResId) {
        val langCode = LanguageManager.getCurrentLanguageCode()
        val locale = Locale(langCode)
        val config = Configuration(context.resources.configuration).apply { setLocale(locale) }
        val localizedContext = context.createConfigurationContext(config)
        localizedContext.getString(textResId)
    }

    val fontFamily = when (currentLanguageCode) {
        "ko" -> FontFamily(Font(R.font.cookierun_regular))
        "en", "tet" -> FontFamily(Font(R.font.gummy_italic_variable))
        else -> FontFamily(Font(R.font.cookierun_regular))
    }

    val iconRes = when {
        !isEnabled -> inactiveIconRes
        isSelected -> activeIconRes
        else -> inactiveIconRes
    }

    val textColor = when {
        !isEnabled -> Color(0xFF919191)
        isSelected -> Color(0xFF000000)
        else -> Color(0xFF919191)
    }

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(iconSize),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        Text(
            text = text,
            fontSize = textSize,
            fontFamily = fontFamily,
            fontWeight = FontWeight.W400,
            color = textColor,
            maxLines = 1
        )
    }
}
