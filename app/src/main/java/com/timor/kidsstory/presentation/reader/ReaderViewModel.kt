package com.timor.kidsstory.presentation.reader

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timor.kidsstory.KidsStoryApplication
import com.timor.kidsstory.domain.model.NavigationDirection
import com.timor.kidsstory.domain.usecase.GetStoryUseCase
import com.timor.kidsstory.domain.usecase.PageNavigationUseCase
import com.timor.kidsstory.presentation.reader.model.PageUiState
import com.timor.kidsstory.presentation.reader.model.ReaderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 읽기 화면 뷰모델
class ReaderViewModel(
    private val getStoryUseCase: GetStoryUseCase,
    private val pageNavigationUseCase: PageNavigationUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ReaderUiState())
    val state = _state.asStateFlow()

    fun loadStory(storyId: String) {
        Log.d("ReaderViewModel", "Loading story: $storyId")
        viewModelScope.launch {
            try {
                getStoryUseCase(storyId).getOrNull()?.let { storyDetail ->
                    Log.d("ReaderViewModel", "Story loaded: ${storyDetail.pages.size} pages")
                    _state.value = ReaderUiState(
                        currentPage = 0,
                        pages = storyDetail.pages.map { page ->
                            PageUiState(
                                imageUrl = page.imageUrl,
                                texts = page.texts,
                                pageNumber = page.pageNumber + 1,
                                totalPages = storyDetail.pages.size
                            )
                        }
                    )
                } ?: run {
                    Log.e("ReaderViewModel", "Story not found for id: $storyId")
                }
            } catch (e: Exception) {
                Log.e("ReaderViewModel", "Error loading story", e)
            }
        }
    }

    fun onPageChanged(newPage: Int) {
        viewModelScope.launch {
            val currentPage = state.value.currentPage
            val totalPages = state.value.pages.size

            try {
                val direction = if (newPage > currentPage) {
                    NavigationDirection.NEXT
                } else {
                    NavigationDirection.PREVIOUS
                }

                val validatedPage = pageNavigationUseCase(currentPage, totalPages, direction)
                _state.value = _state.value.copy(currentPage = validatedPage)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ReaderViewModel::class.java)) {
                        val application = app as KidsStoryApplication
                        return ReaderViewModel(
                            application.getStoryUseCase,
                            application.pageNavigationUseCase,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}