package com.timor.kidsstory.presentation.book.components.pagetest

import androidx.compose.foundation.pager.PagerState

/**
 * 특정 페이지의 오프셋 계산
 *
 * 현재 페이지와 대상 페이지 간의 상대적 위치를 계산합니다.
 * 이 값은 페이지 넘김 효과를 구현할 때 페이지의 위치와 회전 각도를 결정하는 데 사용됩니다.
 *
 * @param page 오프셋을 계산할 페이지 인덱스
 * @return 현재 페이지를 기준으로 한 대상 페이지의 오프셋
 */
fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

/**
 * 페이지의 시작 오프셋 계산
 *
 * 특정 페이지의 오프셋 값 중 0 이상의 값만 반환합니다.
 * 이 값은 페이지가 왼쪽에서 오른쪽으로 이동할 때의 애니메이션에 사용됩니다.
 *
 * @param page 오프셋을 계산할 페이지 인덱스
 * @return 0 이상으로 제한된 페이지 오프셋
 */
fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

/**
 * 페이지의 종료 오프셋 계산
 *
 * 특정 페이지의 오프셋 값 중 0 이하의 값만 반환합니다.
 * 이 값은 페이지가 오른쪽에서 왼쪽으로 이동할 때의 애니메이션에 사용됩니다.
 *
 * @param page 오프셋을 계산할 페이지 인덱스
 * @return 0 이하로 제한된 페이지 오프셋
 */
fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}