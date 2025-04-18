package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState

@Composable
fun BookCover(
    state: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        if (state.coverImage.isNotEmpty()) {
            // 실제 이미지 로드
            AsyncImage(
                model = state.coverImage,
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
    }
}
