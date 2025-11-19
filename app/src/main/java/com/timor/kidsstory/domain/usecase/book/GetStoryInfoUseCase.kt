package com.timor.kidsstory.domain.usecase.book

import com.timor.kidsstory.domain.model.StoryInfo
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 동화 정보(줄거리, 사전/사후 질문)을 가져오는 UseCase
 */
class GetStoryInfoUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(storyId: String, languageCode: String): Result<StoryInfo> {
        return bookRepository.getStoryInfo(storyId, languageCode)
    }
}