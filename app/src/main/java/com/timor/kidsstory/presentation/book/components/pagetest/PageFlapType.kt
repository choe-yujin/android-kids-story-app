package com.timor.kidsstory.presentation.book.components.pagetest

import androidx.compose.ui.graphics.Shape


/*
* 비트맵 회전 담당
* */
internal sealed class PageFlapType(val shape: Shape) {
    data object Top : PageFlapType(TopShape)
    data object Bottom : PageFlapType(BottomShape)
    data object Left : PageFlapType(LeftShape)
    data object Right : PageFlapType(RightShape)
}