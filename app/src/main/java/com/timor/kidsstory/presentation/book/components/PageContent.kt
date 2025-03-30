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

/**
 * 동화책 페이지 컨텐츠를 표시하는 컴포저블
 *
 * 좌우 분할 화면으로 구성되어 있으며, 좌측에는 [PageImageSection]을 통해 동화책 이미지를,
 * 우측에는 [PageTextSection]을 통해 텍스트 내용을 표시합니다.
 * 가로 모드에 최적화된 레이아웃으로 설계되었습니다.
 *
 * @param state 페이지 UI 상태 정보
 * @param onBackToBookshelf 책장으로 돌아가기 버튼 클릭 시 실행할 콜백
 * @param modifier 레이아웃 수정자
 * @param onTextToSpeech 텍스트-음성 변환 실행 콜백
 */
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