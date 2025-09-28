package com.timor.kidsstory.presentation.book.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 동화책 페이지의 이미지 영역 컴포저블
 *
 * 페이지 왼쪽에 위치하며, 동화책의 일러스트레이션을 표시합니다.
 * 좌측 상단에는 책장으로 돌아가는 뒤로가기 버튼이 있습니다.
 * 이미지는 화면에 꽉 차게 표시되며, ContentScale.Crop으로 적절히 크기가 조정됩니다.
 *
 * @param state 페이지 UI 상태 정보
 * @param onBackToBookshelf 책장으로 돌아가기 버튼 클릭 시 실행할 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun PageImageSection(
    state: PageUiState,
    onBackToBookshelf: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxHeight()) {
        // 🔍 디버깅 로그 추가
        if (state.imageUrl.isBlank()) {
            Log.w("PageImageSection", "❌ Empty image URL for page ${state.pageNumber}")
        } else {
            Log.d("PageImageSection", "📷 Loading image: ${state.imageUrl}")
        }
        
        // 페이지 이미지 로드 및 표시
        AsyncImage(
            model = state.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onError = { error ->
                Log.e("PageImageSection", "❌ Failed to load image: ${state.imageUrl}", error.result.throwable)
            },
            onSuccess = {
                Log.d("PageImageSection", "✅ Successfully loaded image: ${state.imageUrl}")
            }
        )

        // 뒤로가기 버튼 - 반투명 원형 배경 (반응형 크기, 2/3로 축소)
        val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp // 48 * 2/3 = 32
        val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp // 32 * 2/3 ≈ 21
        val padding = (16 * ResponsiveTextUtils.getScreenScaleFactor()).dp
        
        Box(
            modifier = Modifier
                .padding(padding)
                .align(Alignment.TopStart)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            IconButton(
                onClick = onBackToBookshelf,
                modifier = Modifier.size(buttonSize)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to bookshelf",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Preview(
    name = "PageImageSection - Korean",
    group = "PageImageSection",
    showBackground = true,
    widthDp = 400,
    heightDp = 360
)
@Composable
fun PageImageSectionPreview() {
    KidsStoryTheme {
        PageImageSection(
            state = PageUiState(
                imageUrl = "file:///android_asset/images/801/book_801_page_1.jpg",
                texts = listOf(
                    "옛날 옛날에"
                ),
                pageNumber = 1,
                totalPages = 14
            ),
            onBackToBookshelf = {},
            modifier = Modifier.fillMaxHeight()
        )
    }
}