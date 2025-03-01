package com.timor.kidsstory.presentation.chatbot

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.domain.util.PermissionRequest

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

    Column(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.messages) { message ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (!message.isFromUser) {
                        Spacer(modifier = Modifier.width(50.dp))
                    }
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(8.dp)
                    )
                    if (message.isFromUser) {
                        Spacer(modifier = Modifier.width(50.dp))
                    }
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (state.error != null) {
            Text(text = "Error: ${state.error}")
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