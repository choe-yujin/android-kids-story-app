package com.timor.kidsstory.presentation.book

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 책 읽기 화면의 루트 컴포저블
 * - 뷰모델과 UI 연결
 * - 네비게이션 이벤트 처리
 *
 * @param viewModel 책 읽기 화면 뷰모델
 * @param onBack 뒤로가기(책장으로 돌아가기) 콜백
 */
@Composable
fun BookScreenRoot(
    viewModel: BookViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    // 책 읽기 화면 UI 컴포넌트 호출
    BookScreen(
        // 뷰모델의 상태를 UI에 연결
        state = viewModel.state.collectAsStateWithLifecycle().value,
        // TTS 관련 상태
        isTetumTtsReady = viewModel.isTetumTtsReady.collectAsStateWithLifecycle().value,
        isDownloadingModel = viewModel.isDownloadingModel.collectAsStateWithLifecycle().value,
        downloadProgress = viewModel.downloadProgress.collectAsStateWithLifecycle().value,
        showTtsDownloadDialog = viewModel.showTtsDownloadDialog.collectAsStateWithLifecycle().value,
        ttsErrorMessage = viewModel.ttsErrorMessage.collectAsStateWithLifecycle().value,
        // 액션 처리 - 모든 액션을 뷰모델에 전달하여 효과음 처리
        onAction = { action ->
            when (action) {
                is BookAction.BackBookShelf -> {
                    viewModel.onAction(action)  // 효과음 재생
                    onBack()  // 뒤로가기 처리
                }
                else -> viewModel.onAction(action)  // 기타 액션은 뷰모델에서 처리
            }
        }
    )
}