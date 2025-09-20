package com.timor.kidsstory.presentation.book.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.timor.kidsstory.R
import com.timor.kidsstory.presentation.book.model.PageUiState
import com.timor.kidsstory.ui.theme.AppTextStyles
import com.timor.kidsstory.ui.theme.KidsStoryTheme
import com.timor.kidsstory.ui.theme.ResponsiveTextUtils

/**
 * 전체 이미지 레이아웃 페이지 컴포넌트
 * 
 * 이미지가 화면 전체를 차지하고, 텍스트는 이미지 하단에 오버레이로 표시됩니다.
 * 주로 표지 페이지나 임팩트 있는 장면에 사용됩니다.
 * 
 * @param pageState 페이지 UI 상태 정보
 * @param currentLanguage 현재 언어 코드 (폰트 선택용)
 * @param onBackToBookshelf 책장으로 돌아가기 콜백
 * @param onTextToSpeech TTS 실행 콜백
 * @param modifier 레이아웃 수정자
 */
@Composable
fun FullImagePageLayout(
    pageState: PageUiState,
    currentLanguage: String = "ko",
    onBackToBookshelf: () -> Unit = {},
    onTextToSpeech: (List<String>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 배경 전체 이미지
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pageState.imageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 이미지를 화면에 꽉 채우도록
        )
        
        // 텍스트가 있는 경우에만 하단 오버레이 표시
        if (pageState.texts.isNotEmpty()) {
            // 하단 그라데이션 오버레이
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                    .padding(24.dp)
            ) {
                // 언어별 폰트 선택 - pretendard로 통일
                val textStyle = AppTextStyles.pretendardMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                // 텍스트 컨테이너
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 각 텍스트를 개별적으로 표시
                    pageState.texts.forEach { text ->
                        if (text.isNotBlank()) {
                            Text(
                                text = text,
                                style = textStyle,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        
        // 상단 컨트롤 버튼들 (SPLIT 화면과 동일한 스타일)
        // 뒤로가기 버튼 (왼쪽 상단)
        if (pageState.pageNumber == 1) { // 첫 페이지에만 표시
            val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp
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
        
        // TTS 버튼 (오른쪽 상단)
        if (pageState.texts.isNotEmpty() && pageState.texts.any { it.isNotBlank() } && pageState.currentLanguageCode != "tetum") {
            val buttonSize = (32 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            val iconSize = (21 * ResponsiveTextUtils.getScreenScaleFactor()).dp
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                IconButton(
                    onClick = { onTextToSpeech(pageState.texts) },
                    modifier = Modifier.size(buttonSize)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Text to speech",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 600, widthDp = 800)
@Composable
private fun FullImagePageLayoutPreview() {
    KidsStoryTheme {
        FullImagePageLayout(
            pageState = PageUiState(
                imageUrl = "", // 프리뷰에서는 빈 이미지
                texts = listOf("이것은 전체 이미지 레이아웃의 예시입니다.", "텍스트는 이미지 하단에 오버레이로 표시됩니다."),
                pageNumber = 1,
                totalPages = 10,
                pageType = "FULL_IMAGE",
                currentLanguageCode = "ko"
            ),
            currentLanguage = "ko",
            onBackToBookshelf = {},
            onTextToSpeech = {}
        )
    }
}
