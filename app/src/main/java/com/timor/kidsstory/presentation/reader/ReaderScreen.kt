package com.timor.kidsstory.presentation.reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.timor.kidsstory.presentation.reader.components.StoryPage
import com.timor.kidsstory.presentation.reader.model.ReaderUiState

// 읽기 화면 UI 컴포넌트
@Composable
fun StoryDetailScreen(
    state: ReaderUiState,
    onBackToBookshelf: () -> Unit,
    onPageChanged: (Int) -> Unit
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
            modifier = Modifier.fillMaxSize()
        )
    }
}