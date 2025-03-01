package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.repository.BookRepository
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBookDetailUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) {
    suspend operator fun invoke(storyId: String): Result<List<Page>> {
        try {
            // 사용자 설정에서 언어 가져오기
            val userPreference = userPreferenceRepository.getUserPreferences().first()
            val language = userPreference.languageCode.ifEmpty { "en-ph" }

            Log.d("GetBookDetailUseCase", "Loading book: $storyId with language: $language")

            // 책 페이지 가져오기
            return bookRepository.getBookPages(storyId, language)

        } catch (e: Exception) {
            Log.e("GetBookDetailUseCase", "Error in GetBookDetailUseCase", e)
            return Result.failure(e)
        }
    }
}