package com.timor.kidsstory.presentation.chatbot

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timor.kidsstory.domain.util.PermissionRequest
import com.timor.kidsstory.presentation.chatbot.components.MessageBox

@Composable
fun ChatbotScreen(
    viewModel: ChatbotScreenViewModel,
    state: ChatbotUiState,
) {
    var hasPermission by remember { mutableStateOf(false) }

    val context = rememberUpdatedState(LocalContext.current)

    PermissionRequest(
        permission = Manifest.permission.RECORD_AUDIO,
        onPermissionGranted = { hasPermission = true },
        onPermissionDenied = { hasPermission = false },
        onShowRationale = {
            Toast.makeText(context.value, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    )

    // 음성 인식 중일 때 다이얼로그 표시
    if (state.isRecording) {
        Dialog(onDismissRequest = { viewModel.stopVoiceSearch() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {

                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = "음성 인식 중 입니다.."
                )

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                Button(
                    onClick = viewModel::stopVoiceSearch,
                    colors = ButtonColors(
                        containerColor = Color(0xFFFDDE5A),
                        contentColor = Color.Black, disabledContentColor = Color.Gray, disabledContainerColor = Color.Gray
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    Text(text = "음성 검색 중지")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E0))
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            reverseLayout = true        // 최근 메시지가 아래 보이도록 설정
        ) {
            items(state.messages) { message ->
                MessageBox(message)
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = state.currentInput,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                viewModel.sendMessage(state.currentInput)
            }) {
                Text("Send")
            }

            IconButton(onClick = viewModel::startVoiceSearch) {
                Icon(Icons.Default.Call, contentDescription = "음성 검색")
            }
        }
    }
}


@Preview
@Composable
private fun ChatbotScreenPreview() {

}