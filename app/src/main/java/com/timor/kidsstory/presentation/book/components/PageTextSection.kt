package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 동화책 페이지의 텍스트 영역 컴포저블 (반응형 개선 버전)
 *
 * 페이지 오른쪽에 위치하며, 동화책의 텍스트 내용을 표시합니다.
 * 화면 크기에 따라 글씨 크기가 자동으로 조정되며,
 * 첫 번째 페이지(표지)는 특별히 큰 글씨로 표시됩니다.
 * 오른쪽 상단에는 텍스트를 음성으로 읽어주는 TTS 기능 버튼이 있고,
 * 오른쪽 하단에는 현재 페이지 번호가 표시됩니다.
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
) {
    // 반응형 패딩 계산
    val responsivePadding = ResponsiveTextUtils.getResponsivePadding().dp
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(responsivePadding)
    ) {
        // 텍스트 내용 표시
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            state.texts.forEachIndexed { index, text ->
                // 첫 번째 텍스트가 제목일 가능성이 높은지 확인
                val isTitle = ResponsiveTextUtils.isLikelyTitle(text, state.pageNumber) || 
                             (index == 0 && state.pageNumber == 0)
                
                // 반응형 텍스트 스타일 적용
                val textStyle = ResponsiveTextUtils.getPageTextStyle(
                    pageNumber = state.pageNumber,
                    isTitle = isTitle
                )
                
                Text(
                    text = text,
                    style = textStyle,
                    textAlign = if (isTitle) TextAlign.Center else TextAlign.Start,
                    modifier = Modifier
                        .padding(
                            vertical = if (isTitle) (responsivePadding * 0.75f) else (responsivePadding * 0.5f)
                        )
                        .then(
                            if (isTitle) Modifier.fillMaxWidth() else Modifier
                        )
                )
            }
        }

        // 페이지 번호 표시 (예: "1/14") - 반응형 스타일 적용
        Text(
            text = state.pageDisplay,
            style = ResponsiveTextUtils.getPageNumberStyle().copy(
                color = AppColors.neutral500
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )

        // 음성 말하기 버튼 - 뒤로가기 버튼과 동일한 디자인, 반응형 크기
        val buttonSize = (48 * ResponsiveTextUtils.getScreenScaleFactor()).dp
        val iconSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
        
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
                modifier = Modifier.size(buttonSize)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Text to speech",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Preview(
    name = "PageTextSection - Title Page",
    group = "PageTextSection",
    showBackground = true,
    widthDp = 400,
    heightDp = 360
)
@Composable
fun PageTextSectionTitlePreview() {
    KidsStoryTheme {
        PageTextSection(
            state = PageUiState(
                imageUrl = "",
                texts = listOf(
                    "락롱꽌과 아우꺼"
                ),
                pageNumber = 0, // 첫 페이지 (제목 페이지)
                totalPages = 14
            ),
            onTextToSpeech = {},
        )
    }
}

@Preview(
    name = "PageTextSection - Content Page Korean",
    group = "PageTextSection", 
    showBackground = true,
    widthDp = 400,
    heightDp = 360
)
@Composable
fun PageTextSectionContentPreview() {
    KidsStoryTheme {
        PageTextSection(
            state = PageUiState(
                imageUrl = "",
                texts = listOf(
                    "옛날 옛날에 락비엣 지역에 락롱꽌이라는 영웅이 살았습니다.",
                    "락롱꽌의 외할아버지는 용의 왕이어서 그 피를 이어받은 락롱꽌은 용맹했고 물위를 마치 땅 위처럼 걸어 다닐 수 있었습니다."
                ),
                pageNumber = 1, // 일반 내용 페이지
                totalPages = 14
            ),
            onTextToSpeech = {},
        )
    }
}

@Preview(
    name = "PageTextSection - Tablet Size",
    group = "PageTextSection",
    showBackground = true,
    widthDp = 800,
    heightDp = 600,
    device = "spec:width=800dp,height=600dp,dpi=160"
)
@Composable
fun PageTextSectionTabletPreview() {
    KidsStoryTheme {
        PageTextSection(
            state = PageUiState(
                imageUrl = "",
                texts = listOf(
                    "옛날 옛날에 락비엣 지역에 락롱꽌이라는 영웅이 살았습니다.",
                    "락롱꽌의 외할아버지는 용의 왕이어서 그 피를 이어받은 락롱꽌은 용맹했고 물위를 마치 땅 위처럼 걸어 다닐 수 있었습니다."
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onTextToSpeech = {},
        )
    }
}

@Preview(
    name = "PageTextSection - Tetum Language",
    group = "PageTextSection",
    showBackground = true,
    widthDp = 400,
    heightDp = 360
)
@Composable
fun PageTextSectionTetumPreview() {
    KidsStoryTheme {
        PageTextSection(
            state = PageUiState(
                imageUrl = "",
                texts = listOf(
                    "Iha tempu uluk, iha heroi ida ho naran Lac Long Quan iha rai Lac Viet.",
                    "Hanesan ninia avó, ne'ebé mak rei dragaun, Lac Long Quan mak bravu no bele la'o iha bee hanesan iha rai."
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onTextToSpeech = {},
        )
    }
}
