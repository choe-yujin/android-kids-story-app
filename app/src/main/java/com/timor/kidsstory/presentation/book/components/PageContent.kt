package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 읽기 화면 하위 컴포넌트들
@Composable
fun PageContent(
    state: PageUiState,
    onBackToBookshelf: () -> Unit,
    modifier: Modifier = Modifier,
    onTextToSpeech: (List<String>) -> Unit
) {
    Row(modifier = modifier.fillMaxSize()) {
        PageImageSection(
            state = state,
            onBackToBookshelf = onBackToBookshelf,
            modifier = Modifier.weight(1f)
        )
        PageTextSection(
            state = state,
            modifier = Modifier
                .weight(1f)
                .background(Color.White),
            onTextToSpeech = onTextToSpeech
        )
    }
}

@Preview(
    name = "PageContent - Korean",
    group = "PageContent",
    showBackground = true,
    widthDp = 800,
    heightDp = 400,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun PageContentPreviewKorean() {
    KidsStoryTheme {
        PageContent(
            state = PageUiState(
                imageUrl = "file:///android_asset/images/801/book_801_page_1.jpg",
                texts = listOf(
                    "옛날 옛날에 락비엣 지역에 락롱꽌이라는 영웅이 살았습니다.",
                    "락롱꽌의 외할아버지는 용의 왕이어서 그 피를 이어받은 락롱꽌은 용맹했고 물위를 마치 땅 위처럼 걸어 다닐 수 있었습니다."
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onBackToBookshelf = {},
            onTextToSpeech = {}
        )
    }
}

@Preview(
    name = "PageContent - Tetum",
    group = "PageContent",
    showBackground = true,
    widthDp = 800,
    heightDp = 400,
    device = "spec:width=800dp,height=360dp,orientation=landscape"
)
@Composable
fun PageContentPreviewTetum() {
    KidsStoryTheme {
        PageContent(
            state = PageUiState(
                imageUrl = "file:///android_asset/images/801/book_801_page_1.jpg",
                texts = listOf(
                    "Iha tempu uluk, iha heroi ida ho naran Lac Long Quan iha rai Lac Viet.",
                    "Hanesan ninia avó, ne'ebé mak rei dragaun, Lac Long Quan mak bravu no bele la'o iha bee hanesan iha rai."
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onBackToBookshelf = {},
            onTextToSpeech = {}
        )
    }
}