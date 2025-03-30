package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.domain.model.DownloadStatus
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState

@Composable
fun BookCover(
    state: BookCoverUiState,
    onClick: () -> Unit,
    onDownloadClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(enabled = state.downloadStatus == DownloadStatus.DOWNLOADED, onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 책 표지 이미지
            if (state.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = state.imageUrl,
                    contentDescription = state.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                // 프리뷰용 회색 박스 (이미지 없을 때)
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(140.dp)
                        .background(Color.Gray)
                )
            }

            // 책 제목
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 다운로드 상태 표시 (다운로드 가능한 경우에만)
            if (state.downloadStatus != DownloadStatus.DOWNLOADED && onDownloadClick != null) {
                when (state.downloadStatus) {
                    DownloadStatus.AVAILABLE -> {
                        Button(
                            onClick = onDownloadClick,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp)
                        ) {
                            Text("다운로드")
                        }
                    }
                    DownloadStatus.DOWNLOADING -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        }
                    }
                    DownloadStatus.FAILED -> {
                        Button(
                            onClick = onDownloadClick,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("다시 시도")
                        }
                    }
                    else -> { /* 다른 상태는 처리하지 않음 */ }
                }
            }
        }
    }
}