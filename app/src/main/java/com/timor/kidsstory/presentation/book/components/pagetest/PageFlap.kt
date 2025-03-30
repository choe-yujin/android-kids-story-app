package com.timor.kidsstory.presentation.book.components.pagetest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * 책 페이지 넘김 효과의 구현 컴포넌트
 *
 * 페이지를 넘길 때 실제 종이책처럼 페이지가 구부러지는 3D 효과를 제공합니다.
 * 이미지 비트맵을 사용하여 페이지의 일부(왼쪽, 오른쪽, 위, 아래)를 회전시켜
 * 자연스러운 페이지 넘김 애니메이션을 구현합니다.
 *
 * @param modifier 레이아웃 수정자
 * @param pageFlap 페이지의 어느 부분을 회전시킬지 결정하는 열거형 값
 * @param imageBitmap 페이지 내용의 비트맵을 제공하는 람다
 * @param state 페이저의 상태
 * @param page 현재 페이지 인덱스
 * @param animatedOverscrollAmount 오버스크롤 애니메이션 값을 제공하는 람다
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BoxScope.PageFlap(
    modifier: Modifier = Modifier,
    pageFlap: PageFlapType,
    imageBitmap: () -> ImageBitmap?,
    state: PagerState,
    page: Int,
    animatedOverscrollAmount: () -> Float = { 0f },
) {
    val density = LocalDensity.current
    // 비트맵의 크기를 Dp 단위로 변환
    val size by remember {
        derivedStateOf {
            imageBitmap()?.let {
                with(density) {
                    DpSize(it.width.toDp(), it.height.toDp())
                }
            } ?: DpSize.Zero
        }
    }

    // 페이지 회전 효과 적용
    Canvas(
        modifier
            .size(size)
            .align(Alignment.TopStart)
            .graphicsLayer {
                shape = pageFlap.shape  // 페이지의 특정 부분만 잘라서 표시
                clip = true

                // 3D 회전 효과를 위한 카메라 거리 설정
                cameraDistance = 65f

                // 페이지 부분에 따른 회전 각도 결정
                when (pageFlap) {
                    is PageFlapType.Top -> {
                        rotationX = min(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverscrollAmount().coerceAtLeast(0f) * -20f
                        )
                    }

                    is PageFlapType.Bottom -> {
                        rotationX = max(
                            (state.startOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverscrollAmount().coerceAtMost(0f) * -20f
                        )
                    }

                    is PageFlapType.Left -> {
                        rotationY = -min(
                            (state.endOffsetForPage(page) * 180f).coerceIn(-90f..0f),
                            animatedOverscrollAmount().coerceAtLeast(0f) * -20f
                        )
                    }

                    is PageFlapType.Right -> {
                        rotationY = -max(
                            (state.startOffsetForPage(page) * 180f).coerceIn(0f..90f),
                            animatedOverscrollAmount().coerceAtMost(0f) * -20f
                        )
                    }
                }
            }
    ) {
        // 비트맵 그리기
        imageBitmap()?.let { imageBitmap ->
            // 원본 이미지 그리기
            drawImage(imageBitmap)

            // 그림자 효과를 위한 투명도 조절 오버레이 그리기
            drawImage(
                imageBitmap,
                colorFilter = ColorFilter.tint(
                    Color.Black.copy(
                        alpha = when (pageFlap) {
                            PageFlapType.Top, PageFlapType.Left -> max(
                                (state.endOffsetForPage(page).absoluteValue * .9f).coerceIn(
                                    0f..1f
                                ), animatedOverscrollAmount() * .3f
                            )

                            PageFlapType.Bottom, PageFlapType.Right -> max(
                                (state.startOffsetForPage(page) * .9f).coerceIn(
                                    0f..1f
                                ), (animatedOverscrollAmount() * -1) * .3f
                            )
                        },
                    )
                )
            )
        }
    }
}