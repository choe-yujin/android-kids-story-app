package com.timor.kidsstory.presentation.book.components.pagetest

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * 페이지 상단 영역을 정의하는 Shape
 *
 * 페이지의 상반부만 포함하는 직사각형 모양을 정의합니다.
 * 페이지를 위에서 아래로 넘기는 효과를 구현할 때 사용됩니다.
 */
val TopShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, 0f, size.width, size.height / 2))
}

/**
 * 페이지 하단 영역을 정의하는 Shape
 *
 * 페이지의 하반부만 포함하는 직사각형 모양을 정의합니다.
 * 페이지를 아래에서 위로 넘기는 효과를 구현할 때 사용됩니다.
 */
val BottomShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, size.height / 2, size.width, size.height))
}

/**
 * 페이지 좌측 영역을 정의하는 Shape
 *
 * 페이지의 왼쪽 절반만 포함하는 직사각형 모양을 정의합니다.
 * 페이지를 왼쪽에서 오른쪽으로 넘기는 효과를 구현할 때 사용됩니다.
 */
val LeftShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
}

/**
 * 페이지 우측 영역을 정의하는 Shape
 *
 * 페이지의 오른쪽 절반만 포함하는 직사각형 모양을 정의합니다.
 * 페이지를 오른쪽에서 왼쪽으로 넘기는 효과를 구현할 때 사용됩니다.
 */
val RightShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Rectangle(Rect(size.width / 2, 0f, size.width, size.height))
}