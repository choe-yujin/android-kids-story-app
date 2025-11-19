package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.animation.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.components.LocalizedPluralText
import com.timor.kidsstory.ui.components.FontPolicy
import com.timor.kidsstory.presentation.util.formatFileSize
import java.text.DecimalFormat

/**
 * 선택된 항목들에 대한 실행 버튼
 * 하단에 고정되어 표시되며, 선택된 항목 수와 총 용량을 보여줍니다.
 */
@Composable
fun SelectionFloatingActionButton(
    selectedBooks: List<Book>,
    actionType: ActionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = selectedBooks.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        FloatingActionButtonContent(
            selectedBooks = selectedBooks,
            actionType = actionType,
            onClick = onClick
        )
    }
}

@Composable
private fun FloatingActionButtonContent(
    selectedBooks: List<Book>,
    actionType: ActionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSize = selectedBooks.sumOf { it.totalSize }
    val selectedCount = selectedBooks.size
    
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(16.dp),
        containerColor = when (actionType) {
            ActionType.DOWNLOAD -> AppColors.blue500
            ActionType.UPDATE -> AppColors.yellowRed500
            ActionType.DELETE -> AppColors.red600
        },
        contentColor = AppColors.neutralWhite,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(
                    when (actionType) {
                        ActionType.DOWNLOAD -> R.drawable.ic_download
                        ActionType.UPDATE -> R.drawable.ic_update
                        ActionType.DELETE -> R.drawable.ic_trash
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            
            Column {
                // 🆕 LocalizedPluralText로 변경하여 복수형 처리
                LocalizedPluralText(
                    pluralsResId = when (actionType) {
                        ActionType.DOWNLOAD -> R.plurals.management_confirm_title_download_plural
                        ActionType.UPDATE -> R.plurals.management_confirm_title_update_plural
                        ActionType.DELETE -> R.plurals.management_confirm_title_delete_plural
                    },
                    quantity = selectedCount,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.neutralWhite
                    ),
                    fontPolicy = FontPolicy.DEFAULT
                )
                
                if (totalSize > 0) {
                    Text(
                        text = formatFileSize(totalSize),
                        fontSize = 12.sp,
                        color = AppColors.neutralWhite.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}