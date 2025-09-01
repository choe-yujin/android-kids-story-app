package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 동화책 페이지의 텍스트 영역 컴포저블 (Clean Architecture 적용)
 *
 * 모든 상태 관리는 ViewModel에서 처리하며, 이 컴포넌트는 순수한 UI 표시만 담당합니다.
 * 스크롤 상태 변경 시 콜백을 통해 ViewModel에 알리고,
 * ViewModel에서 계산된 상태를 받아 UI를 업데이트합니다.
 *
 * @param pageState 페이지 전체 UI 상태 (텍스트, 페이지 번호 등)
 * @param textSectionState 텍스트 섹션 전용 UI 상태 (스크롤 관련)
 * @param pageIndex 현재 페이지 인덱스 (0부터 시작)
 * @param onTextToSpeech 텍스트-음성 변환 실행 콜백
 * @param onLayoutChanged 레이아웃 크기 변경 콜백
 * @param onScrollChanged 스크롤 위치 변경 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun PageTextSection(
    pageState: PageUiState,
    textSectionState: PageTextSectionUiState,
    pageIndex: Int,
    onTextToSpeech: (List<String>) -> Unit,
    onLayoutChanged: (pageIndex: Int, contentHeight: Int, containerHeight: Int) -> Unit,
    onScrollChanged: (pageIndex: Int, scrollOffset: Int, maxScrollOffset: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val responsivePadding = ResponsiveTextUtils.getResponsivePadding().dp
    val scrollState = rememberScrollState(initial = textSectionState.scrollOffset)
    
    // 컨테이너 크기를 저장할 상태
    var containerHeight by remember { mutableStateOf(0) }
    
    // 스크롤 상태 변경 감지 및 ViewModel 업데이트
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value to scrollState.maxValue }
            .distinctUntilChanged()
            .collect { (offset, maxOffset) ->
                onScrollChanged(pageIndex, offset, maxOffset)
            }
    }
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(responsivePadding)
            .onGloballyPositioned { coordinates ->
                // 컨테이너 크기가 변경된 경우에만 업데이트
                val newContainerHeight = coordinates.size.height
                if (newContainerHeight != containerHeight && newContainerHeight > 0) {
                    containerHeight = newContainerHeight
                    onLayoutChanged(pageIndex, textSectionState.contentHeight, newContainerHeight)
                }
            }
    ) {
        // 텍스트 내용 표시
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = (36 * ResponsiveTextUtils.getScreenScaleFactor()).dp)
                .verticalScroll(scrollState)
                .onGloballyPositioned { coordinates ->
                    // 콘텐츠 크기가 변경된 경우에만 업데이트
                    val newContentHeight = coordinates.size.height
                    if (newContentHeight != textSectionState.contentHeight && newContentHeight > 0) {
                        onLayoutChanged(pageIndex, newContentHeight, containerHeight)
                    }
                }
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            pageState.texts.forEachIndexed { index, text ->
                val isTitle = ResponsiveTextUtils.isLikelyTitle(text, pageState.pageNumber - 1) || 
                             (index == 0 && pageState.pageNumber == 1)
                
                val textStyle = ResponsiveTextUtils.getPageTextStyle(
                    pageNumber = pageState.pageNumber - 1,
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

        // 상단 그라데이션 페이드 아웃 + 위쪽 화살표
        if (textSectionState.canScrollUp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .zIndex(1f)
                    .align(Alignment.TopCenter)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.arrow_up_float),
                    contentDescription = "Scroll up",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )
            }
        }
        
        // 하단 그라데이션 페이드 아웃 + 아래쪽 화살표
        if (textSectionState.canScrollDown) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .zIndex(1f)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.arrow_down_float),
                    contentDescription = "Scroll down",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                )
            }
        }

        // 페이지 번호 표시
        Text(
            text = pageState.pageDisplay,
            style = ResponsiveTextUtils.getPageNumberStyle().copy(
                color = AppColors.neutral500
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .zIndex(2f)
        )

        // TTS 버튼
        if (pageState.currentLanguageCode != "tet") { // Conditional rendering for Tetum
            val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .zIndex(2f)
            ) {
                IconButton(
                    onClick = { onTextToSpeech(pageState.texts) },
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
}

// Preview 컴포저블들
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
            pageState = PageUiState(
                imageUrl = "",
                texts = listOf("락롱꽌과 아우꺼"),
                pageNumber = 1,
                totalPages = 14
            ),
            textSectionState = PageTextSectionUiState(),
            pageIndex = 0,
            onTextToSpeech = {},
            onLayoutChanged = { _, _, _ -> },
            onScrollChanged = { _, _, _ -> }
        )
    }
}

@Preview(
    name = "PageTextSection - Scrollable with Indicators",
    showBackground = true,
    widthDp = 400,
    heightDp = 200
)
@Composable
fun PageTextSectionScrollableWithIndicatorsPreview() {
    KidsStoryTheme {
        PageTextSection(
            pageState = PageUiState(
                imageUrl = "",
                texts = listOf(
                    "옛날 옛날에 락비엣 지역에 락롱꽌이라는 영웅이 살았습니다.",
                    "락롱꽌의 외할아버지는 용의 왕이어서 그 피를 이어받은 락롱꽌은 용맹했고 물위를 마치 땅 위처럼 걸어 다닐 수 있었습니다.",
                    "그 당시 락비엣은 황량한 지역이었는데 사람을 해치는 요괴가 많았습니다.",
                    "락롱꽌은 힘이 넘치는 영웅이었기 때문에 사람들을 괴롭히는 요괴를 잡으려고 이곳저곳을 돌아다녔습니다.",
                    "바다로 간 락롱꽌은 커다란 물고기 모습을 한 괴물 응으띤을 봤습니다."
                ),
                pageNumber = 2,
                totalPages = 14
            ),
            textSectionState = PageTextSectionUiState(
                contentHeight = 500,
                containerHeight = 200,
                scrollOffset = 0,
                maxScrollOffset = 300,
                canScrollUp = false,
                canScrollDown = true
            ),
            pageIndex = 1,
            onTextToSpeech = {},
            onLayoutChanged = { _, _, _ -> },
            onScrollChanged = { _, _, _ -> }
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
            pageState = PageUiState(
                imageUrl = "",
                texts = listOf(
                    "옛날 옛날에 락비엣 지역에 락롱꽌이라는 영웅이 살았습니다.",
                    "락롱꽌의 외할아버지는 용의 왕이어서 그 피를 이어받은 락롱꽌은 용맹했고 물위를 마치 땅 위처럼 걸어 다닐 수 있었습니다."
                ),
                pageNumber = 2,
                totalPages = 14
            ),
            textSectionState = PageTextSectionUiState(),
            pageIndex = 1,
            onTextToSpeech = {},
            onLayoutChanged = { _, _, _ -> },
            onScrollChanged = { _, _, _ -> }
        )
    }
}
