package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookShelfScreenRoot(
    viewModel: BookshelfViewModel = hiltViewModel(),
    onBookSelect: (String) -> Unit,
    onChatbotClick: () -> Unit,
    onSettingClick: () -> Unit,
) {
    BookshelfScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onAction = { action ->
            when (action) {
                is BookShelfAction.BookSelect ->  {
                    val selectedBook = viewModel.onBookSelected(action.index)
                    onBookSelect(selectedBook?.storyId ?: "")
                }
                is BookShelfAction.ChatbotClick -> onChatbotClick()
                is BookShelfAction.SettingClick -> onSettingClick()
                else -> {
                    viewModel.onAction(action)
                }
            }
        }
    )
}