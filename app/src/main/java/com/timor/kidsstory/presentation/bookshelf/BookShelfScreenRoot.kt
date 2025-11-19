package com.timor.kidsstory.presentation.bookshelf

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * 책장 화면의 루트 컴포저블 (리팩토링됨)
 * 
 * Clean Architecture:
 * - 3개의 분리된 ViewModel 주입
 * - Navigation 이벤트만 처리
 * - UI 상태는 BookshelfScreen에서 처리
 * 
 * @param onBookSelect 책 선택 콜백 (storyId 전달)
 * @param onChatbotClick 챗봇 화면 네비게이션
 * @param onSettingClick 설정 화면 네비게이션
 * @param onMyPageClick 마이페이지 화면 네비게이션 (optional)
 */
@Composable
fun BookShelfScreenRoot(
    onBookSelect: (String) -> Unit,
    onChatbotClick: () -> Unit,
    onSettingClick: () -> Unit,
    onMyPageClick: () -> Unit = {},
    initialLanguage: String,
    initialLevel: Int,
    wasSkipped: Boolean,
    showLevelResultPopup: Boolean
) {
    // 3개의 분리된 ViewModel 주입
    val bookshelfViewModel: BookshelfViewModel = hiltViewModel()
    val attendanceViewModel: com.timor.kidsstory.presentation.attendance.AttendanceViewModel = hiltViewModel()
    val progressViewModel: com.timor.kidsstory.presentation.progress.ProgressViewModel = hiltViewModel()
    
    // BookshelfScreen에 ViewModel들과 Navigation 콜백 전달
    BookshelfScreen(
        bookshelfViewModel = bookshelfViewModel,
        attendanceViewModel = attendanceViewModel,
        progressViewModel = progressViewModel,
        onBookSelect = { index ->
            // 책 선택 처리: index -> storyId 변환
            val bookshelfState = bookshelfViewModel.state.value
            if (index >= 0 && index < bookshelfState.books.size) {
                val selectedBook = bookshelfState.books[index]
                onBookSelect(selectedBook.storyId)
            }
        },
        onSettingClick = onSettingClick,
        onChatbotClick = onChatbotClick,
        onMyPageClick = onMyPageClick
    )
}
