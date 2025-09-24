package com.timor.kidsstory.presentation.bookshelf.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 더블클릭을 감지할 수 있는 Modifier
 */
@Composable
fun Modifier.onDoubleClick(
    onDoubleClick: () -> Unit,
    onSingleClick: (() -> Unit)? = null
): Modifier {
    var clickCount by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    
    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                clickCount++
                
                // 첫 번째 클릭이면 딜레이 후 단일 클릭인지 더블클릭인지 판단
                if (clickCount == 1) {
                    // 300ms 기다린 후 더블클릭이 없으면 단일클릭으로 처리
                    scope.launch {
                        delay(300)
                        if (clickCount == 1) {
                            onSingleClick?.invoke()
                        }
                        clickCount = 0
                    }
                } else if (clickCount == 2) {
                    // 더블클릭 처리
                    onDoubleClick()
                    clickCount = 0
                }
            }
        )
    }
}