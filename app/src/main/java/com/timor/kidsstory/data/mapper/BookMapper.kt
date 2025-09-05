package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.BookDto
import com.timor.kidsstory.data.dto.ContributorDto
import com.timor.kidsstory.data.dto.PageContentResponse
import com.timor.kidsstory.data.dto.PageDto
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Contributor
import com.timor.kidsstory.domain.model.Page

/**
 * BookDto를 도메인 모델로 변환하는 매퍼 클래스
 * - 데이터 계층(DTO)과 도메인 계층(Book) 간의 변환 담당
 */
object BookMapper {

    /**
     * BookDto를 도메인 Book 객체로 변환
     * - DTO에서 필요한 정보만 추출하여 도메인 모델로 변환
     * - 언어에 따라 적절한 제목 선택
     * - 이미지 경로를 완전한 URL로 구성
     *
     * @param dto 변환할 DTO
     * @param language 사용할 언어 코드
     * @return 도메인 Book 객체
     */
    fun mapToDomain(dto: BookDto, language: String): Book {
        // 기본 ID 추출(예: "801_en-ph" -> "801")
        val baseId = dto.storyId.split("_").firstOrNull() ?: dto.storyId

        // 이미지 전체 경로 구성 (Android Assets 파일 시스템 접근 URL)
        val imageUrl = "file:///android_asset/images/${baseId}/${dto.coverImage}"

        return Book(
            storyId = dto.storyId,
            // 언어에 따라 적절한 제목 선택
            title = when {
                language.startsWith("ko") -> dto.titles.ko
                language.startsWith("tet") -> dto.titles.tet
                language.startsWith("mn") -> dto.titles.mn // Added for Mongolian
                else -> dto.titles.en
            },
            coverImage = imageUrl,  // 절대 경로를 포함한 이미지 URL
            level = dto.level,
            category = dto.category,
            pageCount = dto.pageCount,
            contributors = dto.contributors?.flatMap { (lang, rolesMap) ->
                rolesMap.flatMap { (role, names) ->
                    names.map { name -> Contributor(role = role, name = name, lang = lang) }
                }
            } ?: emptyList(),
            sponsors = dto.sponsors?.flatMap { (_, namesList) -> namesList } ?: emptyList(),
            copyright = dto.copyright,
            originalCopyright = dto.originalCopyright,
            pages = emptyList(), // 이 BookDto는 페이지 정보를 포함하지 않으므로 빈 리스트로 초기화
            isDownloaded = true,  // 기본적으로 앱 내에 포함된 책은 다운로드된 상태
            isBookmarked = false,  // 초기에는 북마크되지 않은 상태
            bookVersion = dto.bookVersion // Added bookVersion
        )
    }
}



fun PageContentResponse.toBook(
    language: String,
    level: Int,
    category: String,
    coverImage: String,
    imageFolderPath: String? = null,
    bookVersion: Int = 1 // Added bookVersion as parameter with default
): Book {
    Log.d("BookMapper", "toBook - imageFolderPath: $imageFolderPath, coverImage: $coverImage")
    val baseId = storyId.split("_").firstOrNull() ?: storyId

    val mappedPages = pages.map {
        PageMapper.mapToDomain(
            pageDto = it,
            storyBaseId = baseId,
            isDownloaded = imageFolderPath != null,
            imageFolderPath = imageFolderPath
        )
    }.sortedBy { it.pageNumber }

    val totalPages = mappedPages.size
    val pagesWithTotalInfo = mappedPages.map { it.copy(totalPages = totalPages) }

    return Book(
        storyId = storyId,
        title = title,
        coverImage = if (imageFolderPath != null) {
            coverImage
        } else {
            "file:///android_asset/images/${baseId}/${coverImage}"
        },
        level = level,
        category = category,
        pageCount = totalPages,
        contributors = contributors?.flatMap { (lang, rolesMap) ->
            rolesMap.flatMap { (role, names) ->
                names.map { name -> Contributor(role = role, name = name, lang = lang) }
            }
        } ?: emptyList(),
        sponsors = sponsors?.flatMap { (_, namesList) -> namesList } ?: emptyList(),
        copyright = copyright,
        originalCopyright = originalCopyright,
        pages = pagesWithTotalInfo,
        isDownloaded = imageFolderPath != null,
        isBookmarked = false,
        bookVersion = bookVersion // Added bookVersion
    )
}
