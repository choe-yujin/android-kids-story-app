package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.presentation.bookshelf.model.ManagementTab
import com.timor.kidsstory.R

/**
 * 관리 모드에서 사용하는 탭바
 * - 전체 (모든 책)
 * - 다운로드 (다운로드 가능한 책)
 * - 업데이트 (업데이트 필요한 책)
 */
@Composable
fun ManagementTabBar(
    selectedTab: ManagementTab,
    onTabSelected: (ManagementTab) -> Unit,
    updateCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = AppColors.neutral100,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ManagementTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            val badgeCount = when (tab) {
                ManagementTab.UPDATE -> updateCount
                else -> 0
            }
            
            ManagementTabItem(
                tab = tab,
                isSelected = isSelected,
                badgeCount = badgeCount,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ManagementTabItem(
    tab: ManagementTab,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected) {
                    AppColors.neutralWhite
                } else {
                    Color.Transparent
                }
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = when (tab) {
                    ManagementTab.ALL -> stringResource(R.string.management_tab_all)
                    ManagementTab.DOWNLOAD -> stringResource(R.string.management_tab_download)
                    ManagementTab.UPDATE -> stringResource(R.string.management_tab_update)
                },
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) {
                    AppColors.neutral800
                } else {
                    AppColors.neutral600
                },
                textAlign = TextAlign.Center
            )
            
            // 업데이트 뱃지
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .background(
                            color = AppColors.yellowRed500,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.neutralWhite
                    )
                }
            }
        }
    }
}


