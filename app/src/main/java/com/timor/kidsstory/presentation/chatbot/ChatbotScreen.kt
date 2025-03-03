package com.timor.kidsstory.presentation.chatbot

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.R
import com.timor.kidsstory.domain.util.PermissionRequest
import com.timor.kidsstory.presentation.chatbot.components.MessageBox
import com.timor.kidsstory.presentation.chatbot.components.VoiceDialog
import com.timor.kidsstory.ui.theme.AppColors
import com.timor.kidsstory.ui.theme.KidsStoryTheme

@Composable
fun ChatbotScreen(
    state: ChatbotUiState,
    onAction: (ChatbotAction) -> Unit,
) {
    var hasPermission by remember { mutableStateOf(false) }

    val context = rememberUpdatedState(LocalContext.current)

    PermissionRequest(
        permission = Manifest.permission.RECORD_AUDIO,
        onPermissionGranted = { hasPermission = true },
        onPermissionDenied = { hasPermission = false },
        onShowRationale = {
            Toast.makeText(context.value, "마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    )

    // 음성 인식 중일 때 다이얼로그 표시
    if (state.isRecording && state.isShowDialog) {
        VoiceDialog(
            onDismissRequest = { onAction(ChatbotAction.ShowDialog(false)) },
            cancelVoiceSearch = { onAction(ChatbotAction.ShowDialog(false)) },
            onCloseClick = { onAction(ChatbotAction.ShowDialog(false)) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5F2FF))
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = Color(0xFF2A78DC))
                .padding(horizontal = 38.dp)

        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = {
                    onAction(ChatbotAction.BackScreen)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null,
                    tint = AppColors.neutralWhite,
                )
            }

            Icon(
                painter = painterResource(R.drawable.chat_logo),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = Color.Unspecified,
            )
        }



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
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AppColors.neutralWhite, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(vertical = 6.dp, horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = state.currentInput,
                onValueChange = { newInput ->
                    onAction(ChatbotAction.InputChange(newInput))
                },
                modifier = Modifier
                    .weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.neutral200,
                    unfocusedContainerColor = AppColors.neutral200,
                    disabledContainerColor = AppColors.neutral200,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(1000.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = {
                onAction(ChatbotAction.VoiceSearch)
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_voice),
                    contentDescription = "음성 검색",
                    tint = Color.Unspecified
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
private fun ChatbotScreenPreview() {
    KidsStoryTheme {
        ChatbotScreen(
            ChatbotUiState(),
            onAction = {}
        )
    }
}