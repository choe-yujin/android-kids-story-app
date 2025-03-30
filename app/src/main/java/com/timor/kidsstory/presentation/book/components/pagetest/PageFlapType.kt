package com.timor.kidsstory.presentation.book.components.pagetest

import androidx.compose.ui.graphics.Shape

/**
 * 페이지 회전 효과 유형을 정의하는 sealed 클래스
 *
 * 페이지 넘김 애니메이션에서 페이지의 어느 부분(상단, 하단, 좌측, 우측)을
 * 회전시킬지 결정하는 유형입니다. 각 유형은 해당 영역을 그리기 위한 Shape를 가집니다.
 *
 * @property shape 해당 영역을 클리핑하기 위한 Shape 객체
 */
internal sealed class PageFlapType(val shape: Shape) {
    /**
     * 페이지의 상단 부분 회전 (수직 방향으로 위에서 아래로 넘기는 효과)
     */
    data object Top : PageFlapType(TopShape)

    /**
     * 페이지의 하단 부분 회전 (수직 방향으로 아래에서 위로 넘기는 효과)
     */
    data object Bottom : PageFlapType(BottomShape)

    /**
     * 페이지의 좌측 부분 회전 (수평 방향으로 왼쪽에서 오른쪽으로 넘기는 효과)
     */
    data object Left : PageFlapType(LeftShape)

    /**
     * 페이지의 우측 부분 회전 (수평 방향으로 오른쪽에서 왼쪽으로 넘기는 효과)
     */
    data object Right : PageFlapType(RightShape)
}