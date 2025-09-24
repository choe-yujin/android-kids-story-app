package com.timor.kidsstory.presentation.book.components.pagetest

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * 페이지 넘김 효과가 있는 페이저 컴포넌트
 *
 * 책 넘기는 효과를 시각적으로 구현한 HorizontalPager입니다.
 * 페이지를 넘길 때 실제 책처럼 페이지가 회전하는 3D 애니메이션을 제공합니다.
 * 페이지 경계 밖으로 스크롤할 때 오버스크롤 애니메이션도 지원합니다.
 *
 * @param state 페이저의 상태를 관리하는 PagerState 객체
 * @param modifier 레이아웃 수정자
 * @param pageContent 각 페이지의 내용을 정의하는 컴포저블
 */
@Composable
fun FlipPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    pageContent: @Composable (Int) -> Unit,
    onOverScrolled: (Float) -> Unit = {}
) {
    // 오버스크롤 효과를 위한 상태 관리
    val overscrollAmount = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { state.isScrollInProgress }.collect {
            if (!it) overscrollAmount.floatValue = 0f
        }
    }

    // 부드러운 오버스크롤 애니메이션
    val animatedOverscrollAmount by animateFloatAsState(
        targetValue = overscrollAmount.floatValue / 500,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = ""
    )

    // 오버스크롤 처리를 위한 중첩 스크롤 연결
    val nestedScrollConnection = rememberFlipPagerOverscroll(
        overscrollAmount = overscrollAmount
    )

    // 기본 HorizontalPager 설정
    HorizontalPager(
        state = state,
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .nestedScroll(nestedScrollConnection),
        pageContent = {
            Content(
                it,
                state,
                pageContent,
                animatedOverscrollAmount
            )
        }
    )
}

/**
 * 페이지 내용 래퍼 컴포넌트
 *
 * 각 페이지의 실제 내용을 감싸고, 페이지 넘김 효과를 적용합니다.
 * 페이지를 비트맵으로 캡처하고 페이지 넘김 시 적절한 회전 효과를 적용합니다.
 *
 * @param page 현재 페이지 인덱스
 * @param state 페이저의 상태
 * @param pageContent 페이지 내용 컴포저블
 * @param animatedOverscrollAmount 오버스크롤 애니메이션 값
 */
@Composable
private fun Content(
    page: Int,
    state: PagerState,
    pageContent: @Composable (Int) -> Unit,
    animatedOverscrollAmount: Float
) {
    // z-인덱스 관리로 페이지 중첩 순서 결정
    var zIndex by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { state.offsetForPage(page) }.collect {
            zIndex = when (state.offsetForPage(page)) {
                in -.5f..(.5f) -> 3f  // 현재 중앙에 있는 페이지가 최상위로
                in -1f..1f -> 2f  // 인접 페이지는 중간 레이어로
                else -> 1f  // 멀리 있는 페이지는 하단 레이어로
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .graphicsLayer {
                val pageOffset = state.offsetForPage(page)
                translationX = size.width * pageOffset  // 페이지 좌우 위치 이동
            },
        contentAlignment = Alignment.Center,
    ) {
        // 페이지 내용을 비트맵으로 캡처하기 위한 상태 관리
        var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
        val graphicsLayer = rememberGraphicsLayer()
        val isImageBitmapNull by remember {
            derivedStateOf {
                imageBitmap == null
            }
        }

        // 실제 페이지 내용을 그리는 Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .alpha(if (state.isScrollInProgress && !isImageBitmapNull) 0f else 1f)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                },
            contentAlignment = Alignment.Center
        ) {
            pageContent(page)  // 실제 페이지 내용 그리기
        }

        // 주기적으로 페이지 내용을 비트맵으로 캡처
        LaunchedEffect(state.isScrollInProgress) {
            while (true) {
                if (graphicsLayer.size.width != 0)
                    imageBitmap = graphicsLayer.toImageBitmap()
                delay(if (state.isScrollInProgress) 16 else 300)  // 스크롤 중에는 더 빠르게 갱신
            }
        }

        // 테마 변경 시 비트맵 다시 캡처
        LaunchedEffect(MaterialTheme.colorScheme.surface) {
            if (graphicsLayer.size.width != 0)
                imageBitmap = graphicsLayer.toImageBitmap()
        }

        // 왼쪽 페이지 넘김 효과 (오른쪽에서 왼쪽으로)
        PageFlap(
            modifier = Modifier.fillMaxSize(),
            pageFlap = PageFlapType.Left,
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverscrollAmount = { animatedOverscrollAmount }
        )

        // 오른쪽 페이지 넘김 효과 (왼쪽에서 오른쪽으로)
        PageFlap(
            modifier = Modifier.fillMaxSize(),
            pageFlap = PageFlapType.Right,
            imageBitmap = { imageBitmap },
            state = state,
            page = page,
            animatedOverscrollAmount = { animatedOverscrollAmount }
        )
    }
}

/**
 * 플립 페이저를 위한 오버스크롤 효과 제공 컴포넌트
 *
 * 사용자가 페이저의 경계를 넘어 스크롤할 때 자연스러운 오버스크롤 효과를 제공합니다.
 * 스크롤 시 페이지가 약간 기울어지는 효과를 만들어 사용자 경험을 향상시킵니다.
 *
 * @param overscrollAmount 오버스크롤 양을 저장하는 상태
 * @return 중첩 스크롤 연결 객체
 */
@Composable
private fun rememberFlipPagerOverscroll(
    overscrollAmount: MutableFloatState
): NestedScrollConnection {
    val nestedScrollConnection = object : NestedScrollConnection {

        /**
         * 오버스크롤 양 계산
         *
         * 사용자의 스크롤 제스처를 기반으로 오버스크롤 양을 계산하고 제한합니다.
         *
         * @param available 사용 가능한 스크롤 양
         */
        private fun calculateOverscroll(available: Float) {
            val previous = overscrollAmount.floatValue
            overscrollAmount.floatValue += available * (.3f)  // 감쇠 효과
            overscrollAmount.floatValue = when {
                previous > 0 -> overscrollAmount.floatValue.coerceAtLeast(0f)  // 양수 방향으로만 증가
                previous < 0 -> overscrollAmount.floatValue.coerceAtMost(0f)   // 음수 방향으로만 감소
                else -> overscrollAmount.floatValue
            }
        }

        // 스크롤 전 처리 - 오버스크롤 중일 때 스크롤 소비
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (overscrollAmount.floatValue != 0f) {
                calculateOverscroll(available.x)
                return available
            }

            return Offset.Zero
        }

        // 스크롤 후 처리 - 남은 스크롤에 대해 오버스크롤 계산
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            calculateOverscroll(available.x)
            return available
        }
    }
    return nestedScrollConnection
}
