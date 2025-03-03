package com.timor.kidsstory.presentation.chatbot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 메세지를 박스 형태로 제작
@Composable
fun UserMessageBox(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = AppColors.blue600,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 8.dp, horizontal = 36.dp),
            text = text,
            style = AppTextStyles.gummyMediumSemibold, // 왜 폰트는 적용 안됨?
            color = AppColors.neutralWhite
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = AppColors.blue600, shape = CircleShape)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_user),
                contentDescription = "user",
                tint = AppColors.neutralWhite,
            )
        }
    }
}

@Preview
@Composable
private fun MessageBoxPreview() {
    KidsStoryTheme {
        UserMessageBox("dfdsf")
    }
}