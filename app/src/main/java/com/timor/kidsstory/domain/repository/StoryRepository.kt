package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.StoryResource

// 스토리 데이터 접근을 위한 인터페이스
interface StoryRepository {
    suspend fun parseEpub(epubFileName: String): Result<StoryResource>
    fun getStoryById(storyId: String): StoryResource?
    suspend fun getStories(): List<StoryResource>
}