package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme


/*
* 메인 - 필터바
* Create by JaeYeon Kim
* @since 2025.04.02
* */
@Composable
fun FilterBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
        ,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterBarButton(text = "All", isSelected = false)

        Icon(
            painter = painterResource(R.drawable.separation_bar),
            tint = AppColors.unknown400,
            contentDescription = null
        )

        FilterBarButton(text = "Stage", isSelected = false)

        Icon(
            painter = painterResource(R.drawable.separation_bar),
            tint = AppColors.unknown400,
            contentDescription = null
        )

        FilterBarButton(text = "Category", isSelected = false)
    }
}

@Composable
fun FilterBarButton(
    text: String,
    isSelected: Boolean
) {
    // 선택 여부에 따라 다르게
    if (!isSelected) {
        Box(
            modifier = Modifier
                .background(AppColors.neutralWhite)
                .border(width = 2.dp, color = AppColors.unknown200, shape = RoundedCornerShape(50.dp))
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                text = text, style = AppTextStyles.gummyMedium.copy(fontSize = 21.sp, color = AppColors.unknown200)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .background(AppColors.unknown300, shape = RoundedCornerShape(size = 50.dp))
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                text = text, style = AppTextStyles.gummyMedium.copy(fontSize = 21.sp, color = AppColors.secondary200)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBarPreview() {
    KidsStoryTheme {
        FilterBar()
    }
}

@Preview(showBackground = true)
@Composable
fun FilterBarButtonPreview() {
    KidsStoryTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterBarButton(text = "Category", isSelected = false)
            FilterBarButton(text = "Stage", isSelected = true)

        }

    }
}