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
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Assets 폴더에서 책 데이터를 로드하는 데이터 소스
 * - JSON 파일 파싱 및 오프라인 데이터 처리
 *
 * @property context 안드로이드 컨텍스트
 */
class AssetDataSource @Inject constructor(
    private val context: Context
) {
    // JSON 파싱을 위한 설정 (알 수 없는 키 무시)
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 메타데이터 JSON에서 모든 책 정보 로드
     *
     * @return DTO 형태의 책 목록
     */
    suspend fun loadBooks(): Result<List<BookDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d("AssetDataSource", "Loading books from metadata JSON")

            // JSON 파일 읽기
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

    /**
     * 특정 책의 페이지 정보 로드
     * - 지정된 언어로 된 JSON 파일 로드 및 파싱
     * - 해당 언어 파일이 없을 경우 영어 버전으로 대체
     *
     * @param storyId 책 ID (예: "801_ko-kr")
     * @param language 언어 코드
     * @return 페이지 정보 DTO
     */
    // AssetDataSource.kt 수정
    suspend fun loadBookPages(storyId: String, language: String): Result<PageContentResponse> =
        withContext(Dispatchers.IO) {
            try {
                // 기본 ID 추출 (801_en-ph -> 801)
                val baseId = storyId.split("_").firstOrNull() ?: storyId

                // 언어 폴더명 결정
                val languageFolder = when {
                    language.startsWith("ko") -> "ko"
                    language.startsWith("tet") -> "tet"
                    else -> "en"
                }

                // 언어 파일명 결정
                val fileName = when {
                    language.startsWith("ko") -> "${baseId}_ko-kr.json"
                    language.startsWith("tet") -> "${baseId}_tetum.json"
                    else -> "${baseId}_en-ph.json"
                }

                // 첫 번째로 시도할 경로: translations/[언어폴더]/[파일명]
                val firstPath = "${languageFolder}/${fileName}"
                Log.d("AssetDataSource", "Trying to load from: translations/$firstPath")

                try {
                    // 지정된 언어 파일 로드 시도
                    val jsonString = context.assets.open("translations/$firstPath").use {
                        it.bufferedReader().readText()
                    }

                    Log.d(
                        "AssetDataSource",
                        "Successfully loaded JSON from translations/$firstPath"
                    )
                    val response = json.decodeFromString<PageContentResponse>(jsonString)
                    Log.d("AssetDataSource", "Parsed ${response.pages.size} pages")

                    return@withContext Result.success(response)
                } catch (e: IOException) {
                    // 파일 못 찾을 경우 영어 버전 시도
                    Log.w(
                        "AssetDataSource",
                        "Failed to load from translations/$firstPath, trying fallback",
                        e
                    )

                    // 영어 버전으로 폴백
                    if (languageFolder != "en") {
                        val fallbackFileName = "${baseId}_en-ph.json"
                        val fallbackPath = "en/$fallbackFileName"
                        Log.d("AssetDataSource", "Trying fallback: translations/$fallbackPath")

                        try {
                            val jsonString = context.assets.open("translations/$fallbackPath").use {
                                it.bufferedReader().readText()
                            }

                            Log.d("AssetDataSource", "Successfully loaded JSON from fallback")
                            val response = json.decodeFromString<PageContentResponse>(jsonString)

                            return@withContext Result.success(response)
                        } catch (e: IOException) {
                            throw e
                        }
                    } else {
                        throw e
                    }
                }
            } catch (e: Exception) {
                Log.e("AssetDataSource", "Final error loading book pages", e)
                Result.failure(e)
            }
        }

    // 외부 저장소에서 책 내용 로드 (다운로드된 책용)
    suspend fun loadExternalBookContent(jsonFilePath: String): Result<PageContentResponse> =
        withContext(Dispatchers.IO) {
            try {
                val file = File(jsonFilePath)
                if (!file.exists()) {
                    Log.e("AssetDataSource", "External book file not found: $jsonFilePath")
                    return@withContext Result.failure(FileNotFoundException("File not found: $jsonFilePath"))
                }

                val jsonString = file.readText()
                val response = json.decodeFromString<PageContentResponse>(jsonString)
                Log.d(
                    "AssetDataSource",
                    "Successfully loaded external book: ${response.storyId} with ${response.pages.size} pages"
                )

                Result.success(response)
            } catch (e: Exception) {
                Log.e("AssetDataSource", "Error loading external book", e)
                Result.failure(e)
            }
        }
}