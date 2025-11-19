package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.UnifiedPageDto
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Page

/**
 * 통합 메타데이터 구조 전용 PageMapper
 * - UnifiedPageDto → Domain Page 변환만 지원
 * - HybridContentManager를 통한 올바른 이미지 경로 결정
 * - 이미지 파일명 자동 생성 규칙 적용
 */
object PageMapper {

    private const val TAG = "PageMapper"

    /**
     * 🆕 리팩토링된 UnifiedPageDto를 도메인 Page 객체로 변환 (비동기)
     */
    suspend fun fromUnified(
        unifiedPage: UnifiedPageDto,
        storyBaseId: String,
        languageCode: String,
        hybridContentManager: HybridContentManager? = null
    ): Page {
        val bookId = storyBaseId.toIntOrNull() ?: 0
        
        // 🆕 이미지 파일명 자동 생성 규칙
        val imageFileName = if (unifiedPage.image.isNotEmpty()) {
            // JSON에 명시된 경우 (레거시 지원)
            unifiedPage.image
        } else {
            // 🆕 규칙 기반 자동 생성
            generateImageFileName(bookId, languageCode, unifiedPage.pageNumber)
        }
        Log.d(TAG, "Generated image file name: $imageFileName for page ${unifiedPage.pageNumber}")

        // 🆕 이미지 경로 - HybridContentManager를 통해 DB 기반으로 결정
        val imageUrl = if (hybridContentManager != null) {
            val url = hybridContentManager.getImageUrl(bookId, imageFileName)
            if (url != null) {
                Log.d(TAG, "📷 Using hybrid path for image: $url")
                url
            } else {
                Log.w(TAG, "📷 Image not found in hybrid storage: $imageFileName")
                ""  // null 대신 빈 문자열
            }
        } else {
            // Fallback: assets 경로 (하위 호환성)
            val fallbackUrl = "file:///android_asset/images/${storyBaseId}/${imageFileName}"
            Log.d(TAG, "📷 Using fallback assets path for image: $fallbackUrl")
            fallbackUrl
        }

        return Page(
            pageNumber = unifiedPage.pageNumber,
            imageUrl = imageUrl,
            texts = unifiedPage.texts,
            pageType = unifiedPage.pageType
        )
    }
    
    /**
     * 이미지 파일명 자동 생성 규칙
     * 
     * - book_{bookId}_page_{pageNumber}.webp (본문 페이지)
     */
    private fun generateImageFileName(
        bookId: Int,
        languageCode: String,
        pageNumber: Int
    ): String {
        return "book_${bookId}_page_${pageNumber}.webp"
    }
}
