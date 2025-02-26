package com.timor.kidsstory.presentation.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.timor.kidsstory.presentation.reader.components.StoryPage
import com.timor.kidsstory.presentation.reader.model.ReaderUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 읽기 화면 UI 컴포넌트
@Composable
fun StoryDetailScreen(
    state: ReaderUiState,
    onBackToBookshelf: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onTextToSpeech: (List<String>) -> Unit
) {
    if (state.pages.isEmpty()) {
        // 로딩 상태나 빈 상태 표시
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Loading...",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.pages.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        StoryPage(
            state = state.pages[pageIndex],
            onBackToBookshelf = onBackToBookshelf,
            modifier = Modifier.fillMaxSize(),
            onTextToSpeech = { content ->
                onTextToSpeech(content)
            }
        )
    }
}

@Preview(showBackground = true, heightDp = 360, widthDp = 800)
@Composable
private fun StoryDetailScreenPreview() {
    KidsStoryTheme {
        StoryDetailScreen(
            state = ReaderUiState(currentPage = 0, pages = listOf()),
            onBackToBookshelf = {},
            onPageChanged = {},
            onTextToSpeech = {}
        )
    }
}