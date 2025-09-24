package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.presentation.bookshelf.model.ManagementActionType

/**
 * 관리 모드 하단 작업 바
 * - 선택된 책들에 대한 일괄 작업 실행
 * - 다운로드 / 업데이트 / 삭제 버튼 표시
 */
@Composable
fun ManagementActionBar(
    downloadCount: Int,
    updateCount: Int,
    deleteCount: Int,
    totalSelectedSize: Long,
    onDownloadClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppColors.neutralWhite,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 선택된 항목 정보
            if (downloadCount + updateCount + deleteCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "선택됨: ${downloadCount + updateCount + deleteCount}권",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.neutral700
                    )
                    
                    if (totalSelectedSize > 0) {
                        Text(
                            text = formatFileSize(totalSelectedSize),
                            fontSize = 12.sp,
                            color = AppColors.neutral600
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // 작업 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 다운로드 버튼
                if (downloadCount > 0) {
                    ActionButton(
                        text = "선택 다운로드 ($downloadCount)",
                        icon = R.drawable.ic_download,
                        backgroundColor = AppColors.blue500,
                        onClick = onDownloadClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 업데이트 버튼
                if (updateCount > 0) {
                    ActionButton(
                        text = "선택 업데이트 ($updateCount)",
                        icon = R.drawable.ic_update,
                        backgroundColor = AppColors.green500,
                        onClick = onUpdateClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 삭제 버튼
                if (deleteCount > 0) {
                    ActionButton(
                        text = "선택 삭제 ($deleteCount)",
                        icon = R.drawable.ic_delete,
                        backgroundColor = AppColors.yellowRed500,
                        onClick = onDeleteClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: Int,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = AppColors.neutralWhite
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 파일 크기를 읽기 쉬운 형식으로 변환
 */
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
    }
}
