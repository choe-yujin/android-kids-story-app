package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.repository.BookRepository
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 특정 책의 상세 페이지 정보를 가져오는 유스케이스
 * - 사용자 언어 설정 적용 및 페이지 데이터 로드
 *
 * @property bookRepository 책 데이터 저장소
 * @property userPreferenceRepository 사용자 설정 저장소
 */
class GetBookDetailUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) {
    /**
     * 특정 책의 모든 페이지 정보를 가져옴
     *
     * @param storyId 책 ID
     * @return 페이지 목록 또는 오류
     */
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