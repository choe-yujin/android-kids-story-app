package com.timor.kidsstory.presentation.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingScreenRoot(
    viewModel: SettingViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    SettingScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onAction = { action ->
            when(action) {
                is SettingAction.BackButtonClick -> onBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}