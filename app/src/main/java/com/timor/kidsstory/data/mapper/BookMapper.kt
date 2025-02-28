package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.BookDto
import com.timor.kidsstory.data.dto.TitlesDto
import com.timor.kidsstory.domain.model.Book
import javax.inject.Inject

class BookMapper @Inject constructor() {
    fun mapToDomain(dto: BookDto, language: String): Book {
        val title = getLocalizedTitle(dto.titles, language)

        // 기본 ID 추출 (예: 801_en-ph -> 801)
        val baseId = dto.storyId.split("_").firstOrNull() ?: dto.storyId

        // 커버 이미지 파일명
        val coverImage = dto.coverImage

        Log.d("BookMapper", "Mapping book: storyId=${dto.storyId}, baseId=$baseId, title=$title, cover=$coverImage")

        return Book(
            storyId = dto.storyId, // 원래 storyId 유지 (예: 801_en-ph)
            title = title,
            coverImage = coverImage, // 메타데이터에서 제공된 파일명 그대로 사용
            level = dto.level,
            category = dto.category,
            pageCount = dto.pageCount,
            isDownloaded = true,
            isBookmarked = false
        )
    }

    private fun getLocalizedTitle(titles: TitlesDto, language: String): String {
        return when {
            language.startsWith("ko") -> titles.ko
            language.startsWith("tet") -> titles.tet
            else -> titles.en
        }
    }
}