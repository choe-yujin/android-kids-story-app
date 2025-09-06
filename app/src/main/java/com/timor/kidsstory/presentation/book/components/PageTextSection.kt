package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.book.model.PageTextSectionUiState
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PageTextSection(
    pageState: PageUiState,
    textSectionState: PageTextSectionUiState,
    pageIndex: Int,
    currentLanguage: String,
    onTextToSpeech: (List<String>) -> Unit,
    onLayoutChanged: (pageIndex: Int, contentHeight: Int, containerHeight: Int) -> Unit,
    onScrollChanged: (pageIndex: Int, scrollOffset: Int, maxScrollOffset: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val responsivePadding = ResponsiveTextUtils.getResponsivePadding().dp
    val scrollState = rememberScrollState(initial = textSectionState.scrollOffset)
    var containerHeight by remember { mutableStateOf(0) }

    // 스크롤 상태 변경 감지
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value to scrollState.maxValue }
            .distinctUntilChanged()
            .collect { (offset, maxOffset) -> onScrollChanged(pageIndex, offset, maxOffset) }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(responsivePadding)
            .onGloballyPositioned { coordinates ->
                val newContainerHeight = coordinates.size.height
                if (newContainerHeight != containerHeight && newContainerHeight > 0) {
                    containerHeight = newContainerHeight
                    onLayoutChanged(pageIndex, textSectionState.contentHeight, newContainerHeight)
                }
            }
    ) {
        // Column 전체 중앙 배치
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .onGloballyPositioned { coordinates ->
                        val newContentHeight = coordinates.size.height
                        if (newContentHeight != textSectionState.contentHeight && newContentHeight > 0) {
                            onLayoutChanged(pageIndex, newContentHeight, containerHeight)
                        }
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                // 페이지 텍스트
                pageState.texts.forEachIndexed { index, text ->
                    val isTitle = ResponsiveTextUtils.isLikelyTitle(text, pageState.pageNumber - 1) ||
                            (index == 0 && pageState.pageNumber == 1)
                    val textStyle = ResponsiveTextUtils.getPageTextStyle(
                        pageNumber = pageState.pageNumber - 1,
                        isTitle = isTitle
                    )
                    Text(
                        text = text,
                        style = textStyle,
                        textAlign = if (isTitle) TextAlign.Center else TextAlign.Start,
                        modifier = Modifier
                            .padding(vertical = if (isTitle) (responsivePadding * 0.75f) else (responsivePadding * 0.5f))
                            .then(if (isTitle) Modifier.fillMaxWidth() else Modifier)
                            .align(if (isTitle) Alignment.CenterHorizontally else Alignment.Start)
                            .offset(y = if (isTitle) (-24.dp) else 0.dp) // Move title up by 16dp
                    )
                }

                // 페이지 1: Contributors, Sponsors
                if (pageState.pageNumber == 1) {
                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredContributors = pageState.contributors.filter { it.lang == currentLanguage }
                    if (filteredContributors.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.Start) {
                            filteredContributors.forEach { contributor ->
                                Text(
                                    text = "${contributor.role} | ${contributor.name}",
                                    style = MaterialTheme.typography.titleSmall,//bodySmall.copy(fontSize = 16.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(bottom = 2.dp) // Added bottom padding
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    pageState.sponsors?.takeIf { it.isNotEmpty() }?.let { sponsors ->
                        val sponsorsLabel = when (currentLanguage) {
                            "ko" -> "후원: "
                            "en" -> "Sponsors: "
                            "tet" -> "Sponsor sira: "
                            "mn" -> "Ивээн тэтгэг치: "
                            else -> "Sponsors: "
                        }
                        Text(
                            text = "$sponsorsLabel${sponsors.joinToString()}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.Start) // Changed to Alignment.Start
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        // 상단 화살표 + 페이드 아웃
        if (textSectionState.canScrollUp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .zIndex(1f)
                    .align(Alignment.TopCenter)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.arrow_up_float),
                    contentDescription = "Scroll up",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )
            }
        }

        // 하단 화살표 + 페이드 아웃
        if (textSectionState.canScrollDown) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .zIndex(1f)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.arrow_down_float),
                    contentDescription = "Scroll down",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                )
            }
        }

        // TTS 버튼
        if (pageState.currentLanguageCode != "tetum") {
            val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    .zIndex(2f)
            ) {
                IconButton(onClick = { onTextToSpeech(pageState.texts) }, modifier = Modifier.size(buttonSize)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Text to speech",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }

        // Bottom Row for Copyright
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Copyright and Original Copyright
            if (pageState.pageNumber == 1) {
                Column(
                    modifier = Modifier
                        .weight(1f), // Take up available space
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${pageState.title} ${pageState.copyright}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                    )

                    pageState.originalCopyright?.let { original ->
                        Text(
                            text = original,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 6.sp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    // 페이지 번호 (original position: bottom-right)
        Text(
            text = pageState.pageDisplay,
            style = ResponsiveTextUtils.getPageNumberStyle().copy(color = AppColors.neutral500),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .zIndex(2f)
        )
    }
}