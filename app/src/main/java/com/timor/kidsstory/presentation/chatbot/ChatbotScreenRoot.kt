package com.timor.kidsstory.presentation.chatbot

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 챗봇 화면의 루트 컴포저블
 * - 뷰모델과 UI 연결
 * - 네비게이션 이벤트 처리
 *
 * @param viewModel 챗봇 화면 뷰모델
 * @param onBack 뒤로가기(책장으로 돌아가기) 콜백
 */
@Composable
fun ChatbotScreenRoot(
    viewModel: ChatbotScreenViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    // 챗봇 화면 UI 컴포넌트 호출
    ChatbotScreen(
        // 뷰모델의 상태를 UI에 연결
        state = viewModel.state.collectAsStateWithLifecycle().value,
        // 액션 처리 - 뒤로가기는 상위 컴포넌트에, 나머지는 뷰모델에 위임
        onAction = { action ->
            when (action) {
                is ChatbotAction.BackScreen -> onBack()  // 뒤로가기 처리
                else -> {
                    viewModel.onAction(action)  // 기타 액션은 뷰모델에서 처리
                }
            }
        }
    )
}
