package com.timor.kidsstory.presentation.book

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookScreenRoot(
    viewModel: BookViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    BookScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onAction = { action ->
            when (action) {
                is BookAction.BackBookShelf -> onBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}