package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
 * @param hasViewedDownloadTab DOWNLOAD 탭 확인 여부 (🆕 하이브리드)
 * @param hasViewedUpdateTab UPDATE 탭 확인 여부 (🆕 하이브리드)
 * @param onTabSelected 탭 선택 콜백
 */
@Composable
fun ManagementTabBar(
    selectedTab: ManagementTab = ManagementTab.DOWNLOAD,
    downloadCount: Int = 0,
    updateCount: Int = 0,
    deleteCount: Int = 0,
    hasViewedDownloadTab: Boolean = false,    // 🆕 하이브리드
    hasViewedUpdateTab: Boolean = false,      // 🆕 하이브리드
    onTabSelected: (ManagementTab) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 16.dp), // FilterBar와 동일한 패딩
        horizontalArrangement = Arrangement.spacedBy(8.dp) // FilterBar와 동일한 간격
    ) {
        ManagementTab.values().forEach { tab ->
            // 🆕 하이브리드: 탭별 스마트 카운트 계산
            val count = when (tab) {
                ManagementTab.DOWNLOAD -> if (hasViewedDownloadTab) 0 else downloadCount  // 🆕 확인했으면 숨김
                ManagementTab.UPDATE -> if (hasViewedUpdateTab) 0 else updateCount      // 🆕 확인했으면 숨김
                ManagementTab.DELETE -> 0 // 🆕 삭제 탭은 카운트 없음
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
 * 관리 탭 버튼 - FilterBarButton과 동일한 스타일 + 🆕 배지 추가
 */
@Composable
private fun ManagementTabButton(
    titleResId: Int,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 🆕 배지 표시 조건: 선택되지 않았고 카운트가 0보다 클 때
    val showBadge = !isSelected && count > 0
    
    Box {
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
        
        // 🆕 배지 (빨간 원, 우상단에 표시)
        if (showBadge) {
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp),
                containerColor = AppColors.red600
            ) {
                Text(
                    text = if (count > 9) "9+" else count.toString(),
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
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
            deleteCount = 10,
            hasViewedDownloadTab = false,  // 🆕 아직 안보음 -> 배지 표시 안됨 (선택됨)
            hasViewedUpdateTab = false     // 🆕 아직 안보음 -> 배지 표시
        )
    }
}

@Preview(showBackground = true, name = "After Viewed")
@Composable
fun ManagementTabBarViewedPreview() {
    KidsStoryTheme {
        ManagementTabBar(
            selectedTab = ManagementTab.UPDATE,
            downloadCount = 5,
            updateCount = 3,
            deleteCount = 10,
            hasViewedDownloadTab = true,   // 🆕 이미 보음 -> 배지 없음
            hasViewedUpdateTab = false     // 🆕 선택되어서 배지 없음
        )
    }
}
