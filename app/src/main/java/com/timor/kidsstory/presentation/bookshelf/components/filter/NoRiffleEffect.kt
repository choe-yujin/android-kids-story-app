package com.timor.kidsstory.presentation.bookshelf.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/*
* 클릭 효과 제거 확장 함수
* */
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}