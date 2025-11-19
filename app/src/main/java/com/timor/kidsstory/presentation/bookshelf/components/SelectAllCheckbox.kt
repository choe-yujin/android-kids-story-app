package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors

/**
 * 관리 모드 "모두 선택" 체크박스
 */
@Composable
fun SelectAllCheckbox(
    isAllSelected: Boolean,
    selectableCount: Int,
    selectedCount: Int,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        color = AppColors.neutral100,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 체크박스
                Checkbox(
                    checked = isAllSelected,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppColors.blue500,
                        uncheckedColor = AppColors.neutral400
                    )
                )
                
                // 텍스트
                Text(
                    text = "모두 선택",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.neutral800
                )
            }
            
            // 선택 개수 표시
            if (selectableCount > 0) {
                Text(
                    text = "$selectedCount / $selectableCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.neutral600
                )
            }
        }
    }
}
