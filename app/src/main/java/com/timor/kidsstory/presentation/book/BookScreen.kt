package com.timor.kidsstory.presentation.book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.orhanobut.logger.Logger
import com.timor.kidsstory.presentation.book.components.PageContent
import com.timor.kidsstory.presentation.book.components.pagetest.FlipPager
import com.timor.kidsstory.presentation.book.model.BookUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import eu.wewox.pagecurl.ExperimentalPageCurlApi
import eu.wewox.pagecurl.page.PageCurl
import eu.wewox.pagecurl.page.rememberPageCurlState
import kotlinx.coroutines.launch

/**
 * 책 읽기 화면 UI 컴포넌트
 * - 페이지 뷰어와 내비게이션 제공
 * - 페이지 전환 애니메이션 지원
 *
 * @param state 책 읽기 화면 UI 상태
 * @param onAction 사용자 액션 처리 콜백
 */
@OptIn(ExperimentalPageCurlApi::class)
@Composable
fun BookScreen(
    state: BookUiState,
    onAction: (BookAction) -> Unit,
) {
    if (state.pages.isEmpty()) {
        // 책 페이지가 로드되지 않았을 때 로딩 표시
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Loading...",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    // 페이저 상태 생성 - 현재 페이지 및 전체 페이지 수 관리
    val pagerState = rememberPagerState(
        initialPage = state.currentPageIndex,
        pageCount = { state.pages.size }
    )

    val pageCurlState = rememberPageCurlState(initialCurrent = state.currentPageIndex)
    val scope = rememberCoroutineScope()        // 테스트를 위한 코루틴 스코프

    // 페이지 변경 감지 및 처리
    LaunchedEffect(pagerState.currentPage) { )
        onAction(BookAction.PageChange(pagerState.currentPage))
    }

    // 새로운 페이지 애니메이션 적용
//    PageCurl(
//        state = pageCurlState,
//        count = state.pages.size,
//        key = { state.pages[it].hashCode() },
//        modifier = Modifier.fillMaxWidth()
//    ) { pageIndex ->
//        PageContent(
//            state = state.pages[pageIndex],
//            onBackToBookshelf = {
//                onAction(BookAction.BackBookShelf)
//            },
//            modifier = Modifier.fillMaxSize(),
//            onTextToSpeech = { content ->
//                onAction(BookAction.TextToSpeak(content))
//            },
//            nextPage = {
//                scope.launch {
//                    pageCurlState.next()
//                }
//            }
//        )
//    }
//
    // Flip 효과를 넣은 Horizontal Pager로 페이지 표시
    FlipPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
    ) { pageIndex ->
        // 개별 페이지 내용 표시
        PageContent(
            state = state.pages[pageIndex],
            onBackToBookshelf = {
                onAction(BookAction.BackBookShelf)
            },
            modifier = Modifier.fillMaxSize(),
            onTextToSpeech = { content ->
                onAction(BookAction.TextToSpeak(content))
            },
            nextPage = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 360, widthDp = 800)
@Composable
private fun BookScreenPreview() {
    KidsStoryTheme {
        BookScreen(
            state = BookUiState(currentPageIndex = 0, pages = listOf()),
            onAction = {}
        )
    }
}