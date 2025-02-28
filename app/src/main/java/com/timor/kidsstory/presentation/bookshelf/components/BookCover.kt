package com.timor.kidsstory.presentation.bookshelf.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState

// 책 표지 UI 컴포넌트
@Composable
fun BookCover(
    state: BookCoverUiState,
    onClick: () -> Unit,
) {
    // storyId에서 기본 ID 추출 (예: 801_en-ph -> 801)
    val baseId = state.storyId.split("_").firstOrNull() ?: state.storyId

    // 메타데이터의 coverImage 필드에는 파일명만 있으므로 경로 구성
    val imagePath = "images/$baseId/${state.imageUrl}"

    Log.d("BookCover", "Loading cover image: $imagePath for storyId: ${state.storyId}")

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = "file:///android_asset/$imagePath",
            contentDescription = state.title,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}