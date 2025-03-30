package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 책장 화면의 루트 컴포저블
 * - 뷰모델과 UI 연결
 * - 네비게이션 이벤트 처리
 *
 * @param viewModel 책장 화면 뷰모델
 * @param onBookSelect 책 선택 시 호출될 콜백
 * @param onChatbotClick 챗봇 버튼 클릭 시 호출될 콜백
 * @param onSettingClick 설정 버튼 클릭 시 호출될 콜백
 */
@Composable
fun BookShelfScreenRoot(
    viewModel: BookshelfViewModel = hiltViewModel(),
    onBookSelect: (String) -> Unit,
    onChatbotClick: () -> Unit,
    onSettingClick: () -> Unit,
) {
    // 책장 UI 컴포넌트 호출
    BookshelfScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onAction = { action ->
            when (action) {
                is BookShelfAction.BookSelect ->  {
                    // 책 선택 처리 및 네비게이션
                    val selectedBook = viewModel.onBookSelected(action.index)
                    onBookSelect(selectedBook?.storyId ?: "")
                }
                is BookShelfAction.ChatbotClick -> onChatbotClick()  // 챗봇 화면으로 이동
                is BookShelfAction.SettingClick -> onSettingClick()  // 설정 화면으로 이동
                else -> {
                    // 기타 액션은 뷰모델에서 처리
                    viewModel.onAction(action)
                }
            }
        }
    )
}