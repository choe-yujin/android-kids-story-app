package com.timor.kidsstory.domain.usecase

import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.model.PageDetail
import com.timor.kidsstory.domain.model.StoryDetail
import com.timor.kidsstory.domain.repository.StoryRepository

// 스토리 조회 통합 유스케이스
class GetStoryUseCase(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(storyId: String): Result<StoryDetail> {
        return try {
            val story = storyRepository.getStoryById(storyId)
                ?: throw IllegalStateException("Story not found: $storyId")

            Result.success(StoryDetail(
                currentPage = 0,
                pages = story.pages.map { page: Page ->  // 타입 명시
                    PageDetail(
                        imageUrl = "${story.storyId}/images/${page.imageFileName}",
                        texts = page.storyTexts,
                        pageNumber = page.pageNumber
                    )
                }
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}