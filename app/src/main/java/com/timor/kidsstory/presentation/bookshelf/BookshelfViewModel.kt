package com.timor.kidsstory.presentation.bookshelf

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.KidsStoryApplication
import com.timor.kidsstory.domain.model.StoryResource
import com.timor.kidsstory.domain.usecase.GetStoriesUseCase
import com.timor.kidsstory.presentation.bookshelf.model.BookCoverUiState
import com.timor.kidsstory.presentation.bookshelf.model.BookshelfUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 책장 화면 뷰모델
class BookshelfViewModel(
    private val getStoriesUseCase: GetStoriesUseCase,
    application: Application
) : AndroidViewModel(application) {
    private var stories: List<StoryResource> = emptyList()
    private val _state = MutableStateFlow(BookshelfUiState())
    val state = _state.asStateFlow()

    fun loadStories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                stories = getStoriesUseCase().getOrThrow()

                _state.value = _state.value.copy(
                    books = stories.map { story ->
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

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(BookshelfViewModel::class.java)) {
                    val application = app as KidsStoryApplication
                    val getStoriesUseCase = GetStoriesUseCase(application.storyRepository)
                    return BookshelfViewModel(getStoriesUseCase, app) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}