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
    val state = viewModel.state.collectAsStateWithLifecycle().value
    
    // 책장 UI 컴포넌트 호출
    BookshelfScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is BookShelfAction.BookSelect ->  {
                    // 뷰모델에 먼저 액션을 전달하여 효과음 재생
                    viewModel.onAction(action)
                    // 책 선택 처리 및 네비게이션
                    if (action.index >= 0 && action.index < state.books.size) {
                        val selectedBook = state.books[action.index]
                        onBookSelect(selectedBook.storyId)
                    }
                }
                is BookShelfAction.ChatbotClick -> {
                    viewModel.onAction(action)  // 효과음 재생
                    onChatbotClick()  // 챗봇 화면으로 이동
                }
                is BookShelfAction.SettingClick -> {
                    viewModel.onAction(action)  // 효과음 재생
                    onSettingClick()  // 설정 화면으로 이동
                }
                else -> {
                    // 기타 액션은 뷰모델에서 처리
                    viewModel.onAction(action)
                }
            }
        }
    )
}
