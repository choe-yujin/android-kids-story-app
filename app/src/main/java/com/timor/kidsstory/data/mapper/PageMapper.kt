package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.UnifiedPageDto
import com.timor.kidsstory.domain.model.Page

/**
 * 통합 메타데이터 구조 전용 PageMapper
 * - UnifiedPageDto → Domain Page 변환만 지원
 */
object PageMapper {

    private const val TAG = "PageMapper"

    /**
     * UnifiedPageDto를 도메인 Page 객체로 변환
     */
    fun fromUnified(
        unifiedPage: UnifiedPageDto,
        storyBaseId: String,
        isDownloaded: Boolean = false,
        imageFolderPath: String? = null
    ): Page {
        // 이미지 파일명 처리
        val imageFileName = if (unifiedPage.image.isNotEmpty()) {
            unifiedPage.image
        } else {
            "book_${storyBaseId}_page_${unifiedPage.pageNumber}.jpg"
        }

        // 이미지 경로 - 다운로드된 책은 외부 저장소, 그 외는 assets 경로 사용
        val imageUrl = if (isDownloaded && imageFolderPath != null) {
            Log.d(TAG, "Using external storage path for image: file://${imageFolderPath}/${imageFileName}")
            "file://${imageFolderPath}/${imageFileName}"
        } else {
            Log.d(TAG, "Using assets path for image: file:///android_asset/images/${storyBaseId}/${imageFileName}")
            "file:///android_asset/images/${storyBaseId}/${imageFileName}"
        }

        return Page(
            pageNumber = unifiedPage.pageNumber,
            imageUrl = imageUrl,
            texts = unifiedPage.texts,
            pageType = unifiedPage.pageType
        )
    }
}
