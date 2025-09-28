package com.timor.kidsstory.data.mapper

import android.util.Log
import com.timor.kidsstory.data.dto.HybridBookMetadata
import com.timor.kidsstory.data.dto.UnifiedBookContent
import com.timor.kidsstory.data.local.database.entity.HybridBookEntity
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Contributor
import java.io.File

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
    suspend fun fromUnified(
        metadata: HybridBookMetadata,
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
                ?: ""  // 🆕 null일 경우 빈 문자열
        } else {
            "file:///android_asset/images/$baseId/cover_${metadata.id}_$normalizedLang.jpg"
        }
        
        // 🆕 페이지 매핑 (비동기)
        val pages = if (content?.pages != null) {
            content.pages.map { unifiedPage ->
                PageMapper.fromUnified(
                    unifiedPage = unifiedPage,
                    storyBaseId = baseId,
                    languageCode = normalizedLang,
                    hybridContentManager = hybridContentManager
                )
            }.sortedBy { it.pageNumber }
        } else {
            emptyList()
        }
        
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

        // Missions 변환
        val missions = content?.missions?.map { missionDto ->
            com.timor.kidsstory.domain.model.Mission(
                title = missionDto.title,
                description = missionDto.description
            )
        } ?: emptyList()

        return Book(
            storyId = storyId,
            title = languageContent.title,
            coverImage = coverImageUrl,
            level = metadata.level,
            category = metadata.category,
            unlockStep = metadata.unlockStep, // 🆕 메타데이터에서 unlockStep 매핑
            pageCount = totalPages,
            contributors = contributors,
            sponsors = emptyList(),
            copyright = content?.copyright ?: "",
            originalCopyright = null,
            pages = pagesWithTotalInfo,
            missions = missions, // 미션 정보 추가
            isDownloaded = true,
            isBookmarked = false,
            bookVersion = languageContent.contentVersion
        )
    }

    /**
     * 🆕 리팩토링된 HybridBookEntity를 도메인 Book으로 변환 (비동기)
     */
    suspend fun fromHybridEntity(
        entity: HybridBookEntity,
        content: UnifiedBookContent? = null,
        hybridContentManager: HybridContentManager? = null,
        metadata: HybridBookMetadata? = null // 🆕 메타데이터 마지막에 추가
    ): Book {
        Log.d("BookMapper", "Mapping from HybridEntity for ${entity.id}_${entity.language}, content is null: ${content == null}")
        val storyId = "${entity.id}_${entity.language}"
        val baseId = entity.id.toString()
        
        // 🆕 커버 이미지 경로 - HybridContentManager를 통해 DB 기반으로 결정
        val coverImageUrl = if (hybridContentManager != null) {
            val coverFileName = "cover_${entity.id}_${entity.language}.jpg"
            hybridContentManager.getImageUrl(entity.id, coverFileName)
                ?: run {
                    // HybridContentManager에서 찾지 못하면 entity 경로 직접 사용
                    Log.w("BookMapper", "Image not found via HybridContentManager, using entity path: ${entity.coverImagePath}")
                    if (entity.coverImagePath.startsWith("file://")) {
                        entity.coverImagePath
                    } else {
                        "file://${entity.coverImagePath}"
                    }
                }
        } else if (entity.coverImagePath.startsWith("file://")) {
            entity.coverImagePath
        } else {
            "file://${entity.coverImagePath}"  // 직접 entity에서 경로 사용
        }
        
        // 🆕 unlockStep 설정: entity에서 우선, 메타데이터로 백업
        val unlockStep = entity.unlockStep.takeIf { it > 0 } ?: metadata?.unlockStep ?: 0
        
        // 🆕 총 용량 계산 (JSON 콘텐츠 + 이미지 파일들)
        val totalSize = if (hybridContentManager != null) {
            calculateBookSize(entity.id, entity.language, hybridContentManager)
        } else {
            0L
        }
        
        // 🆕 tags 설정: 메타데이터에서 가져오거나 entity에서 가져오기
        val tags = metadata?.languages?.get(entity.language)?.tags ?: entity.tags
        
        // 🆕 페이지 매핑 (비동기)
        val pages = if (content?.pages != null) {
            content.pages.map { unifiedPage ->
                PageMapper.fromUnified(
                    unifiedPage = unifiedPage,
                    storyBaseId = baseId,
                    languageCode = entity.language,
                    hybridContentManager = hybridContentManager
                )
            }.sortedBy { it.pageNumber }
        } else {
            emptyList()
        }
        
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

        // Missions 변환
        val missions = content?.missions?.map { missionDto ->
            com.timor.kidsstory.domain.model.Mission(
                title = missionDto.title,
                description = missionDto.description
            )
        } ?: emptyList()

        return Book(
            storyId = storyId,
            title = entity.title,
            coverImage = coverImageUrl,
            level = entity.level,
            category = entity.category,
            unlockStep = unlockStep, // 🆕 메타데이터에서 설정
            pageCount = totalPages,
            contributors = contributors,
            sponsors = emptyList(),
            copyright = content?.copyright ?: "",
            originalCopyright = null,
            pages = pagesWithTotalInfo,
            missions = missions, // 미션 정보 추가
            isDownloaded = true,
            isBookmarked = false,
            bookVersion = entity.contentVersion,
            totalSize = totalSize, // 🆕 총 용량 설정
            tags = tags // 🆕 메타데이터에서 설정
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
    
    /**
     * 🆕 책의 총 용량 계산 (JSON 콘텐츠 + 이미지 파일들)
     */
    private fun calculateBookSize(
        bookId: Int,
        languageCode: String,
        hybridContentManager: HybridContentManager
    ): Long {
        return try {
            var totalSize = 0L
            
            // 1. JSON 콘텐츠 파일 크기
            val contentPath = hybridContentManager.getContentPath(bookId, languageCode)
            val contentFile = java.io.File(contentPath)
            if (contentFile.exists()) {
                totalSize += contentFile.length()
            }
            
            // 2. 커버 이미지 크기
            val coverPath = hybridContentManager.getImagePath(bookId, "cover_${bookId}_$languageCode.jpg")
            val coverFile = java.io.File(coverPath)
            if (coverFile.exists()) {
                totalSize += coverFile.length()
            }
            
            // 3. 모든 이미지 파일들 크기
            val imagesDir = java.io.File(hybridContentManager.getImagePath(bookId, "")).parentFile
            if (imagesDir?.exists() == true) {
                imagesDir.listFiles()?.forEach { imageFile ->
                    if (imageFile.isFile) {
                        totalSize += imageFile.length()
                    }
                }
            }
            
            Log.d("BookMapper", "📁 Book $bookId ($languageCode) total size: ${totalSize / 1024}KB")
            totalSize
            
        } catch (e: Exception) {
            Log.w("BookMapper", "Failed to calculate book size for $bookId", e)
            0L
        }
    }
}
