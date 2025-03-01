package com.timor.kidsstory.data.local.assets

import android.content.Context
import android.util.Log
import com.timor.kidsstory.data.dto.PageContentResponse
import com.timor.kidsstory.data.dto.StoriesResponse
import com.timor.kidsstory.data.dto.BookDto
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AssetDataSource @Inject constructor(
    private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    // 메타데이터 JSON에서 모든 책 정보 로드
    suspend fun loadBooks(): Result<List<BookDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d("AssetDataSource", "Loading books from metadata JSON")

            val jsonString = context.assets.open("metadata/stories-metadata.json").use {
                it.bufferedReader().readText()
            }

            Log.d("AssetDataSource", "Metadata JSON loaded, parsing...")
            val response = json.decodeFromString<StoriesResponse>(jsonString)
            Log.d("AssetDataSource", "Found ${response.stories.size} books in metadata")

            Result.success(response.stories)
        } catch (e: Exception) {
            Log.e("AssetDataSource", "Error loading books from metadata", e)
            Result.failure(e)
        }
    }

    // 특정 책의 페이지 정보 로드
    suspend fun loadBookPages(storyId: String, language: String): Result<PageContentResponse> = withContext(Dispatchers.IO) {
        try {
            // 기본 ID 추출 (예: 801_en-ph -> 801)
            val baseId = storyId.split("_").firstOrNull() ?: storyId

            // 언어 접미사 결정
            val languageSuffix = when {
                language.startsWith("ko") -> "ko-kr"
                language.startsWith("tet") -> "tetum"
                else -> "en-ph"
            }

            // 정확한 파일 경로 구성 (예: 801_en-ph.json)
            val fileName = "${baseId}_${languageSuffix}.json"

            // 첫 번째로 시도할 경로: translations/[언어코드]/[파일명]
            val firstPath = "${languageSuffix.split("-").firstOrNull() ?: "en"}/$fileName"
            Log.d("AssetDataSource", "Trying to load from: translations/$firstPath")

            try {
                val jsonString = context.assets.open("translations/$firstPath").use {
                    it.bufferedReader().readText()
                }

                Log.d("AssetDataSource", "Successfully loaded JSON from translations/$firstPath")
                val response = json.decodeFromString<PageContentResponse>(jsonString)
                Log.d("AssetDataSource", "Parsed ${response.pages.size} pages")

                return@withContext Result.success(response)
            } catch (e: IOException) {
                Log.w("AssetDataSource", "Failed to load from translations/$firstPath, trying fallback", e)

                // 영어 버전으로 폴백
                if (languageSuffix != "en-ph") {
                    val fallbackFileName = "${baseId}_en-ph.json"
                    val fallbackPath = "en/$fallbackFileName"
                    Log.d("AssetDataSource", "Trying fallback: translations/$fallbackPath")

                    val jsonString = context.assets.open("translations/$fallbackPath").use {
                        it.bufferedReader().readText()
                    }

                    Log.d("AssetDataSource", "Successfully loaded JSON from fallback")
                    val response = json.decodeFromString<PageContentResponse>(jsonString)

                    return@withContext Result.success(response)
                } else {
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e("AssetDataSource", "Final error loading book pages", e)
            Result.failure(e)
        }
    }
}