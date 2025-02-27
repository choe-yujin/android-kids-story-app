package com.timor.kidsstory.presentation.reader.components.pagetest

import androidx.compose.foundation.pager.PagerState

fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}