package com.timor.kidsstory.presentation.book.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 동화책 페이지 컨텐츠를 표시하는 컴포저블 (Clean Architecture 적용)
 *
 * 좌우 분할 화면으로 구성되어 있으며, 좌측에는 [PageImageSection]을 통해 동화책 이미지를,
 * 우측에는 [PageTextSection]을 통해 텍스트 내용을 표시합니다.
 * 가로 모드에 최적화된 레이아웃으로 설계되었습니다.
 *
 * @param pageState 페이지 UI 상태 정보
 * @param textSectionState 텍스트 섹션 상태 정보
 * @param pageIndex 현재 페이지 인덱스
 * @param onBackToBookshelf 책장으로 돌아가기 버튼 클릭 시 실행할 콜백
 * @param onTextToSpeech 텍스트-음성 변환 실행 콜백
 * @param onLayoutChanged 레이아웃 변경 콜백
 * @param onScrollChanged 스크롤 변경 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun PageContent(
    pageState: PageUiState,
    textSectionState: PageTextSectionUiState,
    pageIndex: Int,
    currentLanguage: String, // Added
    onBackToBookshelf: () -> Unit,
    onTextToSpeech: (List<String>) -> Unit,
    onLayoutChanged: (pageIndex: Int, contentHeight: Int, containerHeight: Int) -> Unit,
    onScrollChanged: (pageIndex: Int, scrollOffset: Int, maxScrollOffset: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) { // Wrap Row in a Box for overlay
        Row(modifier = Modifier.fillMaxSize()) { // Original Row
            PageImageSection(
                state = pageState,
                onBackToBookshelf = onBackToBookshelf,
                modifier = Modifier.weight(1f)
            )
            PageTextSection(
                pageState = pageState,
                textSectionState = textSectionState,
                pageIndex = pageIndex,
                currentLanguage = currentLanguage, // Added
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