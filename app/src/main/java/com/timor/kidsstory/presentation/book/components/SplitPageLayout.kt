package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 좌우 분할 레이아웃 페이지 컴포넌트
 * 
 * 기존 동화책의 표준 레이아웃으로, 좌측에 이미지를 우측에 텍스트를 배치합니다.
 * 가로 모드에 최적화되어 있으며, 대부분의 동화책 페이지에서 사용됩니다.
 * 
 * @param pageState 페이지 UI 상태 정보
 * @param textSectionState 텍스트 섹션 상태 정보
 * @param pageIndex 현재 페이지 인덱스
 * @param currentLanguage 현재 언어 코드 (폰트 선택용)
 * @param onBackToBookshelf 책장으로 돌아가기 버튼 클릭 시 실행할 콜백
 * @param onTextToSpeech 텍스트-음성 변환 실행 콜백
 * @param onLayoutChanged 레이아웃 변경 콜백
 * @param onScrollChanged 스크롤 변경 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun SplitPageLayout(
    pageState: PageUiState,
    textSectionState: PageTextSectionUiState,
    pageIndex: Int,
    currentLanguage: String,
    onBackToBookshelf: () -> Unit,
    onTextToSpeech: (List<String>) -> Unit,
    onLayoutChanged: (pageIndex: Int, contentHeight: Int, containerHeight: Int) -> Unit,
    onScrollChanged: (pageIndex: Int, scrollOffset: Int, maxScrollOffset: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 왼쪽: 이미지 섹션
            PageImageSection(
                state = pageState,
                onBackToBookshelf = onBackToBookshelf,
                modifier = Modifier.weight(1f)
            )
            
            // 오른쪽: 텍스트 섹션
            PageTextSection(
                pageState = pageState,
                textSectionState = textSectionState,
                pageIndex = pageIndex,
                currentLanguage = currentLanguage,
                onTextToSpeech = onTextToSpeech,
                onLayoutChanged = onLayoutChanged,
                onScrollChanged = onScrollChanged,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 600, widthDp = 800)
@Composable
private fun SplitPageLayoutPreview() {
    KidsStoryTheme {
        SplitPageLayout(
            pageState = PageUiState(
                imageUrl = "",
                texts = listOf("이것은 좌우 분할 레이아웃의 예시입니다."),
                pageNumber = 1,
                totalPages = 10,
                pageType = "SPLIT"
            ),
            textSectionState = PageTextSectionUiState(),
            pageIndex = 0,
            currentLanguage = "ko",
            onBackToBookshelf = {},
            onTextToSpeech = {},
            onLayoutChanged = { _, _, _ -> },
            onScrollChanged = { _, _, _ -> }
        )
    }
}
