package com.timor.kidsstory.presentation.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.timor.kidsstory.presentation.reader.model.ImageSectionUiState
import com.timor.kidsstory.presentation.reader.model.PageUiState

// 읽기 화면 하위 컴포넌트들
@Composable
fun StoryPage(
    state: PageUiState,
    onBackToBookshelf: () -> Unit,
    modifier: Modifier = Modifier,
    onTextToSpeech: (List<String>) -> Unit
) {
    Row(modifier = modifier.fillMaxSize()) {
        StoryImageSection(
            state = ImageSectionUiState(imageUrl = state.imageUrl),
            onBackToBookshelf = onBackToBookshelf,
            modifier = Modifier.weight(1f)
        )
        StoryTextSection(
            state = state,
            modifier = Modifier
                .weight(1f)
                .background(Color.White),
            onTextToSpeech = onTextToSpeech
        )
    }
}