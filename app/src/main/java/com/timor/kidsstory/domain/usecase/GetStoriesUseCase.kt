package com.timor.kidsstory.domain.usecase

import com.timor.kidsstory.domain.model.StoryResource
import com.timor.kidsstory.domain.repository.StoryRepository
import javax.inject.Inject

// 책장에서 전체 스토리 목록 조회 유스케이스
class GetStoriesUseCase @Inject constructor(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(): Result<List<StoryResource>> {
        return try {
            Result.success(storyRepository.getStories())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}