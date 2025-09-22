package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.UnifiedPageDto
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Page

/**
 * 통합 메타데이터 구조 전용 PageMapper
 * - UnifiedPageDto → Domain Page 변환만 지원
 * - HybridContentManager를 통한 올바른 이미지 경로 결정
 */
object PageMapper {

    private const val TAG = "PageMapper"

    /**
     * UnifiedPageDto를 도메인 Page 객체로 변환
     */
    fun fromUnified(
        unifiedPage: UnifiedPageDto,
        storyBaseId: String,
        hybridContentManager: HybridContentManager? = null
    ): Page {
        // 이미지 파일명 처리
        val imageFileName = if (unifiedPage.image.isNotEmpty()) {
            unifiedPage.image
        } else {
            "book_${storyBaseId}_page_${unifiedPage.pageNumber}.jpg"
        }

        // 이미지 경로 - HybridContentManager를 통해 올바른 경로 결정
        val imageUrl = if (hybridContentManager != null) {
            val bookId = storyBaseId.toIntOrNull() ?: 0
            val url = hybridContentManager.getImageUrl(bookId, imageFileName)
            Log.d(TAG, "Using hybrid path for image: $url")
            url
        } else {
            // Fallback: assets 경로 (하위 호환성)
            val fallbackUrl = "file:///android_asset/images/${storyBaseId}/${imageFileName}"
            Log.d(TAG, "Using fallback assets path for image: $fallbackUrl")
            fallbackUrl
        }

        return Page(
            pageNumber = unifiedPage.pageNumber,
            imageUrl = imageUrl,
            texts = unifiedPage.texts,
            pageType = unifiedPage.pageType
        )
    }
}
