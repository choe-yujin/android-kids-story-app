package com.timor.kidsstory.presentation.chatbot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timor.kidsstory.ui.theme.KidsStoryTheme

@Composable
fun ChatbotScreen(
    viewModel: ChatbotScreenViewModel,
    state: ChatbotUiState,
) {

    Column(modifier = Modifier.padding(16.dp)) {
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

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = state.currentInput,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = viewModel::sendMessage) {
                Text("Send")
            }
        }
    }
}