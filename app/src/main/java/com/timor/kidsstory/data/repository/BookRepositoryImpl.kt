package com.timor.kidsstory.data.repository

import android.util.Log
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.data.mapper.PageMapper
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
) : BookRepository {

    override suspend fun getBooks(language: String): Result<List<Book>> {
        Log.d("BookRepositoryImpl", "Getting books for language: $language")

        return try {
            assetDataSource.loadBooks().map { storyDtos ->
                val languagePrefix = when {
                    language.startsWith("ko") -> "ko"
                    language.startsWith("tet") -> "tet"
                    else -> "en"
                }

                // 해당 언어로 된 책만 필터링
                val filteredBooks = storyDtos.filter {
                    it.storyId.contains(languagePrefix, ignoreCase = true)
                }

                Log.d("BookRepositoryImpl", "Found ${filteredBooks.size} books for language $language")

                // BookMapper가 내부적으로 이미지 경로를 처리
                filteredBooks.map { BookMapper.mapToDomain(it, language) }
            }
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting books", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookById(storyId: String, language: String): Result<Book?> {
        // 입력받은 storyId에서 기본 ID 추출 (801_en-ph -> 801)
        val baseId = storyId.split("_").firstOrNull() ?: storyId

        Log.d("BookRepositoryImpl", "Getting book by ID: $baseId, full storyId: $storyId")

        return try {
            val allBooks = assetDataSource.loadBooks().getOrThrow()

            Log.d("BookRepositoryImpl", "Total books in metadata: ${allBooks.size}")

            // ID가 baseId로 시작하는 모든 책들 찾기
            val matchedBooks = allBooks.filter { book ->
                // storyId에서 기본 ID 부분 추출 (예: 801_en-ph -> 801)
                val bookBaseId = book.storyId.split("_").firstOrNull() ?: book.storyId
                Log.d("BookRepositoryImpl", "Comparing $bookBaseId with $baseId")
                bookBaseId == baseId
            }

            Log.d("BookRepositoryImpl", "Found ${matchedBooks.size} books with base ID: $baseId")

            if (matchedBooks.isEmpty()) {
                Log.e("BookRepositoryImpl", "No books found with base ID: $baseId")

                // 추가 로깅: 모든 책의 ID 출력하여 디버깅
                Log.d("BookRepositoryImpl", "Available book IDs: ${allBooks.map { it.storyId }}")

                return Result.failure(Exception("책을 찾을 수 없습니다: ID $baseId"))
            }

            // 언어에 맞는 책 찾기
            val languagePrefix = when {
                language.startsWith("ko") -> "ko"
                language.startsWith("tet") -> "tet"
                else -> "en"
            }

            // 해당 언어로 된 책 찾기, 없으면 영어 버전으로 대체
            val selectedBook = matchedBooks.find {
                it.storyId.contains(languagePrefix, ignoreCase = true)
            } ?: matchedBooks.find {
                it.storyId.contains("en", ignoreCase = true)
            } ?: matchedBooks.first()

            Log.d("BookRepositoryImpl", "Selected book: ${selectedBook.storyId}")

            Result.success(BookMapper.mapToDomain(selectedBook, language))
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting book by ID", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookPages(storyId: String, language: String): Result<List<Page>> {
        // storyId에서 기본 ID 추출
        val baseId = storyId.split("_").firstOrNull() ?: storyId

        Log.d("BookRepositoryImpl", "Getting book pages for base ID: $baseId, language: $language")

        return try {
            // 언어에 맞는 파일 이름 결정
            val languageCode = when {
                language.startsWith("ko") -> "ko-kr"
                language.startsWith("tet") -> "tetum"
                else -> "en-ph"
            }

            val fullStoryId = "${baseId}_${languageCode}"
            Log.d("BookRepositoryImpl", "Loading pages for full story ID: $fullStoryId")

            // 페이지 로드 및 매핑
            val pagesResult = assetDataSource.loadBookPages(fullStoryId, language)

            pagesResult.map { response ->
                // 개별 페이지 매핑
                val mappedPages = response.pages.map { pageDto ->
                    PageMapper.mapToDomain(pageDto, baseId)
                }.sortedBy { it.pageNumber }

                // totalPages 정보 추가
                PageMapper.addTotalPagesInfo(mappedPages)
            }
        } catch (e: Exception) {
            Log.e("BookRepositoryImpl", "Error getting book pages", e)
            Result.failure(e)
        }
    }
}