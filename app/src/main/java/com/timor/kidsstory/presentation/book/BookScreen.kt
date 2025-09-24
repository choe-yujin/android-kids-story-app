package com.timor.kidsstory.presentation.book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.util.Log
import com.timor.kidsstory.presentation.book.components.CompletionDialog
import com.timor.kidsstory.presentation.book.components.PageContent
import com.timor.kidsstory.presentation.book.components.pagetest.FlipPager
import com.timor.kidsstory.presentation.book.model.BookUiState
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme

/**
 * 책 읽기 화면 UI 컴포넌트 (Clean Architecture 적용)
 * - 페이지 뷰어와 내비게이션 제공
 * - 페이지 전환 애니메이션 지원
 * - 완독 축하 화면 표시
 * - 텍스트 섹션 상태 관리 지원
 *
 * @param state 책 읽기 화면 UI 상태
 * @param onAction 사용자 액션 처리 콜백
 */
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

    Box(modifier = Modifier.fillMaxSize()) {
        // 페이저 상태 생성 - 현재 페이지 및 전체 페이지 수 관리
        val pagerState = rememberPagerState(
            initialPage = state.currentPageIndex,
            pageCount = { state.pages.size }
        )

        // 페이지 변경 감지 및 처리
        LaunchedEffect(pagerState.currentPage) {
            Log.d("BookScreen", "[PageChangeEffect] Current page: ${pagerState.currentPage}, Total pages: ${state.pages.size}")
            if (pagerState.currentPage < state.pages.size) {
                Log.d("BookScreen", "[PageChangeEffect] Dispatching BookAction.PageChange(${pagerState.currentPage})")
                onAction(BookAction.PageChange(pagerState.currentPage))
            }
        }
        
        // 마지막 페이지에서 오버스크롤 감지
        LaunchedEffect(pagerState) {
            var hasTriggeredCompletion = false
            var wasOverSwiping = false // Track if we were in an over-swiping state
            snapshotFlow { 
                Triple(
                    pagerState.currentPage,
                    pagerState.currentPageOffsetFraction,
                    pagerState.isScrollInProgress
                )
            }.collect { (currentPage, offsetFraction, isScrollInProgress) ->
                Log.d("BookScreen", "[OverScrollEffect] CurrentPage: $currentPage, Offset: $offsetFraction, IsScrollInProgress: $isScrollInProgress, Total: ${state.pages.size}")
                
                // Check if we are on the last page
                if (currentPage == state.pages.size - 1) {
                    // If a right swipe (negative offset) is detected, set wasOverSwiping
                    if (offsetFraction < 0) {
                        wasOverSwiping = true
                    } else {
                        // If offset becomes positive or zero, reset wasOverSwiping
                        wasOverSwiping = false
                    }

                    // If an over-swipe was detected (wasOverSwiping was true) AND scroll has just ended AND completion not yet triggered
                    if (wasOverSwiping && !isScrollInProgress && !hasTriggeredCompletion) {
                        Log.d("BookScreen", "[OverScrollEffect] Over-swipe detected on last page and scroll ended! Dispatching completion action...")
                        onAction(BookAction.PageChange(state.pages.size)) // totalPages를 넘는 인덱스로 액션 전달
                        hasTriggeredCompletion = true // Set flag after triggering
                    } else if (isScrollInProgress) {
                        // If scroll is in progress, reset hasTriggeredCompletion to allow re-triggering on next over-swipe
                        hasTriggeredCompletion = false
                    }
                } else {
                    // Not on the last page, reset all flags
                    hasTriggeredCompletion = false
                    wasOverSwiping = false
                }
            }
        }

        // Flip 효과를 넣은 Horizontal Pager로 페이지 표시
        FlipPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            // 개별 페이지 내용 표시
            PageContent(
                pageState = state.pages[pageIndex],
                textSectionState = state.pages[pageIndex].textSectionState,
                pageIndex = pageIndex,
                currentLanguage = state.pages[pageIndex].currentLanguageCode, // Added
                onBackToBookshelf = {
                    onAction(BookAction.BackBookShelf)
                },
                onTextToSpeech = { content ->
                    onAction(BookAction.TextToSpeak(content))
                },
                onLayoutChanged = { pageIdx, contentHeight, containerHeight ->
                    onAction(
                        BookAction.UpdateTextSectionLayout(
                            pageIndex = pageIdx,
                            contentHeight = contentHeight,
                            containerHeight = containerHeight
                        )
                    )
                },
                onScrollChanged = { pageIdx, scrollOffset, maxScrollOffset ->
                    onAction(
                        BookAction.UpdateTextSectionScroll(
                            pageIndex = pageIdx,
                            scrollOffset = scrollOffset,
                            maxScrollOffset = maxScrollOffset
                        )
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 완독 축하 다이얼로그 표시
        CompletionDialog(
            isVisible = state.showCompletionScreen,
            mission = state.mission,
            onConfirm = {
                onAction(BookAction.CompletionConfirmed)
                onAction(BookAction.BackBookShelf) // 확인 후 책장으로 이동
            }
        )
    }
}

@Preview(showBackground = true, heightDp = 360, widthDp = 800)
@Composable
private fun BookScreenPreview() {
    KidsStoryTheme {
        BookScreen(
            state = BookUiState(
                currentPageIndex = 0, 
                pages = listOf(
                    PageUiState(
                        imageUrl = "",
                        texts = listOf("Preview Text"),
                        pageNumber = 1,
                        totalPages = 1,
                        textSectionState = PageTextSectionUiState()
                    )
                )
            ),
            onAction = {}
        )
    }
}
