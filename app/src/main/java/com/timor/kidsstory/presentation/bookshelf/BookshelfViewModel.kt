package com.timor.kidsstory.presentation.bookshelf

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.domain.model.StoryResource
import com.timor.kidsstory.domain.usecase.GetStoriesUseCase
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 책장 화면 뷰모델
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val getStoriesUseCase: GetStoriesUseCase,
    application: Application
) : AndroidViewModel(application) {
    private var stories: List<StoryResource> = emptyList()
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

    init {
        loadStories()
    }

    // 처음 시작될때 load
    private fun loadStories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                stories = getStoriesUseCase().getOrThrow()

                _state.value = _state.value.copy(
                    books = stories.map { story ->
                        println(story.storyId)
                        BookCoverUiState(
                            imageUrl = "${story.storyId}/images/${story.pages[0].imageFileName}",
                            title = story.title
                        )
                    },
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onBookSelected(index: Int): StoryResource? = stories.getOrNull(index)
}