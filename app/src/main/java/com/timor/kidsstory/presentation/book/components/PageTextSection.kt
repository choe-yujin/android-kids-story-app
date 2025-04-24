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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

/**
 * 동화책 페이지의 텍스트 영역 컴포저블
 *
 * 페이지 오른쪽에 위치하며, 동화책의 텍스트 내용을 표시합니다.
 * 오른쪽 상단에는 텍스트를 음성으로 읽어주는 TTS 기능 버튼이 있고,
 * 오른쪽 하단에는 현재 페이지 번호가 표시됩니다.
 * 텍스트는 중앙에 배치되며, 여러 줄의 텍스트가 있는 경우 세로로 나열됩니다.
 *
 * @param state 페이지 UI 상태 정보
 * @param modifier 레이아웃 수정자
 * @param onTextToSpeech 텍스트-음성 변환 실행 콜백
 */
@Composable
fun PageTextSection(
    state: PageUiState,
    modifier: Modifier = Modifier,
    onTextToSpeech: (List<String>) -> Unit,
    nextPage: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // 텍스트 내용 표시
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

        // 페이지 번호 표시 (예: "1/14")
        Text(
            text = state.pageDisplay,
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


        Button(
            onClick = nextPage,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(text = "Next Page")
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
            onTextToSpeech = {},
            nextPage = {}
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
            onTextToSpeech = {},
            nextPage = {}
        )
    }
}