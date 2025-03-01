package com.timor.kidsstory.presentation.book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.timor.kidsstory.presentation.book.components.PageContent
import com.timor.kidsstory.presentation.book.components.pagetest.FlipPager
import com.timor.kidsstory.presentation.book.model.BookUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

// 읽기 화면 UI 컴포넌트
@Composable
fun BookScreen(
    state: BookUiState,
    onBackToBookshelf: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onTextToSpeech: (List<String>) -> Unit,
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
        initialPage = state.currentPageIndex,
        pageCount = { state.pages.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    // Flip 효과를 넣은 Horizontal Pager
    FlipPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
    ) { pageIndex ->
        PageContent(
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
private fun BookScreenPreview() {
    KidsStoryTheme {
        BookScreen(
            state = BookUiState(currentPageIndex = 0, pages = listOf()),
            onBackToBookshelf = {},
            onPageChanged = {},
            onTextToSpeech = {}
        )
    }
}