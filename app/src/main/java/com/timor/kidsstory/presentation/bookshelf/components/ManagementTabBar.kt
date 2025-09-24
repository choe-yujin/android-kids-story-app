package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.presentation.bookshelf.model.ManagementTab
import com.timor.kidsstory.ui.components.FontPolicy
import com.timor.kidsstory.ui.components.LocalizedText
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 관리 모드 탭 바 - FilterBar와 동일한 디자인 톤앤매너 유지
 * 
 * @param selectedTab 현재 선택된 탭
 * @param downloadCount 다운로드 가능한 책 수
 * @param updateCount 업데이트 가능한 책 수
 * @param deleteCount 삭제 가능한 책 수
 * @param onTabSelected 탭 선택 콜백
 */
@Composable
fun ManagementTabBar(
    selectedTab: ManagementTab = ManagementTab.DOWNLOAD,
    downloadCount: Int = 0,
    updateCount: Int = 0,
    deleteCount: Int = 0,
    onTabSelected: (ManagementTab) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 16.dp), // FilterBar와 동일한 패딩
        horizontalArrangement = Arrangement.spacedBy(8.dp) // FilterBar와 동일한 간격
    ) {
        ManagementTab.values().forEach { tab ->
            val count = when (tab) {
                ManagementTab.DOWNLOAD -> downloadCount
                ManagementTab.UPDATE -> updateCount
                ManagementTab.DELETE -> deleteCount
            }
            
            ManagementTabButton(
                titleResId = tab.titleResId,
                count = count,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

/**
 * 관리 탭 버튼 - FilterBarButton과 동일한 스타일
 */
@Composable
private fun ManagementTabButton(
    titleResId: Int,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (!isSelected) {
        // 선택 안 됨: FilterBar 스타일 (흰색 배경 + 회색 테두리)
        Box(
            modifier = Modifier
                .background(AppColors.neutralWhite, shape = RoundedCornerShape(50.dp))
                .border(width = 2.dp, color = AppColors.unknown200, shape = RoundedCornerShape(50.dp))
                .clickable(onClick = onClick)
        ) {
            LocalizedText(
                resId = titleResId,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = AppTextStyles.gummyMedium,
                fontSize = 21.sp,
                color = AppColors.unknown200,
                fontPolicy = FontPolicy.DEFAULT
            )
        }
    } else {
        // 선택됨: FilterBar 스타일 (회색 배경 + 노란색 텍스트)
        Box(
            modifier = Modifier
                .background(AppColors.unknown300, shape = RoundedCornerShape(50.dp))
                .clickable(onClick = onClick)
        ) {
            LocalizedText(
                resId = titleResId,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = AppTextStyles.gummyMedium,
                fontSize = 21.sp,
                color = AppColors.secondary200,
                fontPolicy = FontPolicy.DEFAULT,
                formatArgs = if (count > 0) arrayOf(count) else null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManagementTabBarPreview() {
    KidsStoryTheme {
        ManagementTabBar(
            selectedTab = ManagementTab.DOWNLOAD,
            downloadCount = 5,
            updateCount = 3,
            deleteCount = 10
        )
    }
}
