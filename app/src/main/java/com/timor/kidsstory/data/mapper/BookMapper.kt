package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.UnifiedBookMetadata
import com.timor.kidsstory.data.dto.UnifiedBookContent
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Contributor

/**
 * 통합 메타데이터 구조 전용 BookMapper
 * - UnifiedBookMetadata + UnifiedBookContent → Domain Book 변환
 * - 기존 구조 지원 안함 (완전 새로운 구조만 지원)
 */
object BookMapper {

    /**
     * 통합 메타데이터 구조를 도메인 Book으로 변환
     */
    fun fromUnified(
        metadata: UnifiedBookMetadata,
        languageCode: String,
        content: UnifiedBookContent? = null
    ): Book {
        val normalizedLang = normalizeLanguageCode(languageCode)
        val languageContent = metadata.languages[normalizedLang] 
            ?: metadata.languages.values.first()
        
        val storyId = "${metadata.id}_${normalizedLang}"
        val baseId = metadata.id.toString()
        
        // 커버 이미지 경로
        val coverImageUrl = "file:///android_asset/images/$baseId/cover_${metadata.id}_$normalizedLang.jpg"
        
        // 페이지 매핑
        val pages = content?.pages?.map { unifiedPage ->
            PageMapper.fromUnified(
                unifiedPage = unifiedPage,
                storyBaseId = baseId,
                isDownloaded = true,
                imageFolderPath = null
            )
        }?.sortedBy { it.pageNumber } ?: emptyList()
        
        val totalPages = pages.size
        val pagesWithTotalInfo = pages.map { it.copy(totalPages = totalPages) }
        
        // Contributors 변환
        val contributors = content?.contributors?.flatMap { (role, names) ->
            names.map { name -> 
                Contributor(
                    role = role, 
                    name = name, 
                    lang = normalizedLang
                ) 
            }
        } ?: emptyList()

        return Book(
            storyId = storyId,
            title = languageContent.title,
            coverImage = coverImageUrl,
            level = metadata.level,
            category = metadata.category,
            pageCount = totalPages,
            contributors = contributors,
            sponsors = emptyList(),
            copyright = content?.copyright ?: "",
            originalCopyright = null,
            pages = pagesWithTotalInfo,
            isDownloaded = true,
            isBookmarked = false,
            bookVersion = languageContent.contentVersion
        )
    }

    /**
     * 언어 코드 정규화
     */
    private fun normalizeLanguageCode(language: String): String {
        return when {
            language.startsWith("ko") -> "ko"
            language.startsWith("tet") -> "tet" 
            language.startsWith("en") -> "en"
            language.startsWith("mn") -> "mn"
            else -> "en"
        }
    }
}
