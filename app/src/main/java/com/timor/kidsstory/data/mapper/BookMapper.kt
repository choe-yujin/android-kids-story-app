package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.UnifiedBookMetadata
import com.timor.kidsstory.data.dto.UnifiedBookContent
import com.timor.kidsstory.data.local.database.entity.HybridBookEntity
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Contributor

/**
 * 통합 메타데이터 구조 전용 BookMapper
 * - UnifiedBookMetadata + UnifiedBookContent → Domain Book 변환
 * - HybridBookEntity + UnifiedBookContent → Domain Book 변환
 * - 기존 구조 지원 안함 (완전 새로운 구조만 지원)
 */
object BookMapper {

    /**
     * 통합 메타데이터 구조를 도메인 Book으로 변환
     */
    fun fromUnified(
        metadata: UnifiedBookMetadata,
        languageCode: String,
        content: UnifiedBookContent? = null,
        hybridContentManager: HybridContentManager? = null
    ): Book {
        val normalizedLang = normalizeLanguageCode(languageCode)
        val languageContent = metadata.languages[normalizedLang] 
            ?: metadata.languages.values.first()
        
        val storyId = "${metadata.id}_${normalizedLang}"
        val baseId = metadata.id.toString()
        
        // 커버 이미지 경로 - HybridContentManager를 통해 결정
        val coverImageUrl = if (hybridContentManager != null) {
            hybridContentManager.getImageUrl(metadata.id, "cover_${metadata.id}_$normalizedLang.jpg")
        } else {
            "file:///android_asset/images/$baseId/cover_${metadata.id}_$normalizedLang.jpg"
        }
        
        // 페이지 매핑
        val pages = content?.pages?.map { unifiedPage ->
            PageMapper.fromUnified(
                unifiedPage = unifiedPage,
                storyBaseId = baseId,
                hybridContentManager = hybridContentManager
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
     * HybridBookEntity를 도메인 Book으로 변환
     */
    fun fromHybridEntity(
        entity: HybridBookEntity,
        content: UnifiedBookContent? = null,
        hybridContentManager: HybridContentManager? = null
    ): Book {
        val storyId = "${entity.id}_${entity.language}"
        val baseId = entity.id.toString()
        
        // 커버 이미지 경로 - HybridContentManager를 통해 결정
        val coverImageUrl = if (hybridContentManager != null) {
            val coverFileName = "cover_${entity.id}_${entity.language}.jpg"
            hybridContentManager.getImageUrl(entity.id, coverFileName)
        } else if (entity.coverImagePath.startsWith("file://")) {
            entity.coverImagePath
        } else {
            "file://${entity.coverImagePath}"
        }
        
        // 페이지 매핑
        val pages = content?.pages?.map { unifiedPage ->
            PageMapper.fromUnified(
                unifiedPage = unifiedPage,
                storyBaseId = baseId,
                hybridContentManager = hybridContentManager
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
                    lang = entity.language
                ) 
            }
        } ?: emptyList()

        return Book(
            storyId = storyId,
            title = entity.title,
            coverImage = coverImageUrl,
            level = entity.level,
            category = entity.category,
            pageCount = totalPages,
            contributors = contributors,
            sponsors = emptyList(),
            copyright = content?.copyright ?: "",
            originalCopyright = null,
            pages = pagesWithTotalInfo,
            isDownloaded = true,
            isBookmarked = false,
            bookVersion = entity.contentVersion
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
