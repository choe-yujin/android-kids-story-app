package com.timor.kidsstory.data.mapper

import com.timor.kidsstory.data.dto.BookDto
import com.timor.kidsstory.domain.model.Book

object BookMapper {

    /**
     * BookDto를 도메인 Book 객체로 변환
     *
     * @param dto 변환할 DTO
     * @param language 사용할 언어 코드
     * @param coverImagePath 커버 이미지 전체 경로
     * @return 도메인 Book 객체
     */
    fun mapToDomain(dto: BookDto, language: String): Book {
        // 기본 ID 추출
        val baseId = dto.storyId.split("_").firstOrNull() ?: dto.storyId

        // 이미지 전체 경로 구성
        val imageUrl = "file:///android_asset/images/${baseId}/${dto.coverImage}"

        return Book(
            storyId = dto.storyId,
            title = when {
                language.startsWith("ko") -> dto.titles.ko
                language.startsWith("tet") -> dto.titles.tet
                else -> dto.titles.en
            },
            coverImage = imageUrl,  // 절대 경로를 포함한 이미지 URL
            level = dto.level,
            category = dto.category,
            pageCount = dto.pageCount,
            isDownloaded = true,
            isBookmarked = false
        )
    }
}