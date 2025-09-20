package com.timor.kidsstory.data.local.assets

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

/**
 * 통합 메타데이터 구조 전용 데이터 소스
 * - 새로운 통합 구조만 지원 (기존 호환성 제거)
 * - 내장/다운로드 통합 처리
 */
class UnifiedDataSource @Inject constructor(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * 통합 메타데이터 로드
     * @param metadataPath null이면 내장 Assets, 경로 지정시 외부 파일
     */
    suspend fun loadBooksMetadata(metadataPath: String? = null): Result<UnifiedBooksMetadata> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("UnifiedDataSource", "Loading metadata from: ${metadataPath ?: "assets"}")

                val metadataJson = if (metadataPath != null) {
                    // 외부 파일에서 로드
                    File(metadataPath).readText()
                } else {
                    // 내장 Assets에서 로드
                    context.assets.open("books_metadata.json").use {
                        it.bufferedReader().readText()
                    }
                }

                val unifiedMetadata = json.decodeFromString<UnifiedBooksMetadata>(metadataJson)
                Log.d("UnifiedDataSource", "Found ${unifiedMetadata.books.size} books")

                Result.success(unifiedMetadata)
            } catch (e: Exception) {
                Log.e("UnifiedDataSource", "Error loading books metadata", e)
                Result.failure(e)
            }
        }

    /**
     * 개별 책 콘텐츠 로드
     * @param contentBasePath null이면 내장 Assets, 경로 지정시 외부 파일
     */
    suspend fun loadBookContent(
        bookId: Int,
        language: String,
        contentBasePath: String? = null
    ): Result<UnifiedBookContent> = withContext(Dispatchers.IO) {
        try {
            val languageCode = normalizeLanguageCode(language)
            val fileName = "${bookId}_${languageCode}.json"

            val contentJson = if (contentBasePath != null) {
                // 외부 파일에서 로드
                val contentFile = File(contentBasePath, fileName)
                Log.d("UnifiedDataSource", "Loading external content: ${contentFile.absolutePath}")
                contentFile.readText()
            } else {
                // 내장 Assets에서 로드
                val assetPath = "content/$fileName"
                Log.d("UnifiedDataSource", "Loading asset content: $assetPath")
                context.assets.open(assetPath).use {
                    it.bufferedReader().readText()
                }
            }

            val unifiedContent = json.decodeFromString<UnifiedBookContent>(contentJson)
            Log.d("UnifiedDataSource", "Loaded ${unifiedContent.pages.size} pages for book $bookId")

            Result.success(unifiedContent)
        } catch (e: Exception) {
            Log.e("UnifiedDataSource", "Error loading book content for $bookId", e)
            Result.failure(e)
        }
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
