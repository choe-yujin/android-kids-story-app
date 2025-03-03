package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 읽기 화면 하위 컴포넌트들
@Composable
fun PageTextSection(
    state: PageUiState,
    modifier: Modifier = Modifier,
    onTextToSpeech: (List<String>) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center
        ) {
            state.texts.forEach { text ->
                Text(
                    text = text,
                    style = AppTextStyles.pretendardLargeMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Text(
            text = state.pageDisplay,  // "1/14" ~ "14/14"
            style = AppTextStyles.pretendardSmallMedium.copy(
                color = AppColors.neutral500
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )

        // 음성 말하기 버튼 - 뒤로가기 버튼과 동일한 디자인
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            IconButton(
                onClick = { onTextToSpeech(state.texts) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Text to speech",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(
    name = "PageTextSection - English",
    group = "PageTextSection",
    showBackground = true,
    widthDp = 400,
    heightDp = 360
)
@Composable
fun PageTextSectionPreviewEnglish() {
    KidsStoryTheme {
        PageTextSection(
            state = PageUiState(
                imageUrl = "", // Not used in this section
                texts = listOf(
                    "Once upon a time, there was a hero named Lac Long Quan in the land of the Lac Viet.",
                    "Like his grandfather, who was the king of dragons, Lac Long Quan was brave and could walk on water like on land."
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onTextToSpeech = {}
        )
    }
}

@Preview(
    name = "PageTextSection - Korean",
    group = "PageTextSection",
    showBackground = true,
    widthDp = 400,
    heightDp = 360
)
@Composable
fun PageTextSectionPreviewKorean() {
    KidsStoryTheme {
        PageTextSection(
            state = PageUiState(
                imageUrl = "", // Not used in this section
                texts = listOf(
                    "옛날 옛날에 락비엣 지역에 락롱꽌이라는 영웅이 살았습니다.",
                    "락롱꽌의 외할아버지는 용의 왕이어서 그 피를 이어받은 락롱꽌은 용맹했고 물위를 마치 땅 위처럼 걸어 다닐 수 있었습니다."
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onTextToSpeech = {}
        )
    }
}