package com.timor.kidsstory.presentation.bookshelf.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import java.io.File

// 책 표지 UI 컴포넌트
@Composable
fun BookCover(
    state: BookCoverUiState,
    onClick: () -> Unit
) {
    // storyId에서 기본 ID 추출 (예: 801_en-ph -> 801)
    val baseId = state.storyId.split("_").firstOrNull() ?: state.storyId

    // 메타데이터의 coverImage 필드에는 파일명만 있으므로 경로 구성
    val imagePath = "images/$baseId/${state.imageUrl}"

    Log.d("BookCover", "Loading cover image: $imagePath for storyId: ${state.storyId}")

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(240.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // 올바른 이미지 경로로 assets에서 로드
            AsyncImage(
                model = "file:///android_asset/$imagePath",
                contentDescription = state.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = state.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}