package com.timor.kidsstory.presentation.chatbot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.presentation.chatbot.ChatMessage

// 메세지를 박스 형태로 제작
@Composable
fun MessageBox(message: ChatMessage) {
    val backgroundColor = if (message.isFromUser) Color(0xFF262626) else Color(0xFFFDDE5A)
    val arrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color(0xFFC5FF7A),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}