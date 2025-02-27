package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.PageDto
import com.timor.kidsstory.domain.model.Page
import javax.inject.Inject

class PageMapper @Inject constructor() {
    fun mapToDomain(dto: PageDto, storyId: String): Page {
        // 기본 ID 추출 (예: 801_en-ph -> 801)
        val baseId = storyId.split("_").firstOrNull() ?: storyId

        // 페이지 번호에 따라 적절한 이미지 파일명 결정
        val imageFileName = "book_${baseId}_page_${dto.pageNumber}.jpg"

        Log.d("PageMapper", "Mapping page ${dto.pageNumber} for story $storyId, image: $imageFileName")

        return Page(
            pageNumber = dto.pageNumber,
            imageFileName = imageFileName,
            storyTexts = dto.texts
        )
    }
}