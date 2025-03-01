package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState

@Composable
fun BookCover(
    state: BookCoverUiState,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = state.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}