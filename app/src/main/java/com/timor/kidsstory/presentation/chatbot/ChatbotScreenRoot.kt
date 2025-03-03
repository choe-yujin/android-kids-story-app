package com.timor.kidsstory.presentation.chatbot

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun ChatbotScreenRoot(
    viewModel: ChatbotScreenViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {

    ChatbotScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onAction = { action ->
            when (action) {
                is ChatbotAction.BackScreen -> onBack()
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}
