package com.timor.kidsstory.presentation.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.usecase.book.GetBooksUseCase
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 책장 화면 뷰모델
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

    // 실제 Book 객체 저장
    private var bookList = listOf<Book>()

    init {
        loadStories()
    }

    private fun loadStories() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                Log.d("BookshelfViewModel", "Loading books...")
                val result = getBooksUseCase()

                result.fold(
                    onSuccess = { books ->
                        // 책 목록 저장
                        bookList = books
                        Log.d("BookshelfViewModel", "Books loaded: ${books.size}")

                        _state.update {
                            it.copy(
                                books = books.map { book ->
                                    Log.d("BookshelfViewModel", "Creating UI state for book: ${book.title}, ID: ${book.storyId}, Cover: ${book.coverImage}")

                                    // 메타데이터에서 제공된 coverImage 필드는 파일명만 포함하므로 그대로 사용
                                    BookCoverUiState(
                                        imageUrl = book.coverImage,
                                        title = book.title,
                                        storyId = book.storyId
                                    )
                                },
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e("BookshelfViewModel", "Error loading books: ${error.message}", error)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "책을 불러올 수 없습니다: ${error.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("BookshelfViewModel", "Exception in loadStories", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun onBookSelected(index: Int): Book? {
        if (index < 0 || index >= bookList.size) {
            Log.e("BookshelfViewModel", "Invalid book index: $index")
            return null
        }

        val selectedBook = bookList[index]
        Log.d("BookshelfViewModel", "Book selected: ${selectedBook.storyId}")
        return selectedBook
    }
}