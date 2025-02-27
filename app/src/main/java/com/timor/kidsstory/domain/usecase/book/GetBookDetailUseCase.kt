package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.PageDetail
import com.timor.kidsstory.domain.model.StoryDetail
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

class GetBookDetailUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(storyId: String): Result<StoryDetail> {
        // 기본 언어 코드 설정 (나중에 언어 선택 기능 구현 시 변경 예정)
        val defaultLanguage = "en-ph"

        // ID에서 기본 부분 추출 (예: 801_en-ph -> 801)
        val baseId = storyId.split("_").firstOrNull() ?: storyId

        Log.d("GetBookDetailUseCase", "Loading book: $baseId with language: $defaultLanguage")

        val bookResult = bookRepository.getBookById(baseId, defaultLanguage)
        if (bookResult.isFailure) {
            val error = bookResult.exceptionOrNull()
            Log.e("GetBookDetailUseCase", "Failed to get book: ${error?.message}")
            return Result.failure(error ?: Exception("책을 찾을 수 없습니다"))
        }

        val book = bookResult.getOrNull() ?: return Result.failure(Exception("책을 찾을 수 없습니다"))
        Log.d("GetBookDetailUseCase", "Book found: ${book.title}")

        val pagesResult = bookRepository.getBookPages(baseId, defaultLanguage)
        if (pagesResult.isFailure) {
            val error = pagesResult.exceptionOrNull()
            Log.e("GetBookDetailUseCase", "Failed to get pages: ${error?.message}")
            return Result.failure(error ?: Exception("페이지를 불러올 수 없습니다"))
        }

        val pages = pagesResult.getOrNull() ?: emptyList()
        Log.d("GetBookDetailUseCase", "Loaded ${pages.size} pages")

        if (pages.isEmpty()) {
            return Result.failure(Exception("이 책에는 페이지가 없습니다"))
        }

        return Result.success(
            StoryDetail(
                currentPage = 0,
                pages = pages.map { page ->
                    val imageFileName = page.imageFileName
                    Log.d("GetBookDetailUseCase", "Creating page detail, image: $imageFileName")

                    PageDetail(
                        // 이미지 경로는 기본 ID를 포함해야 함 (예: "801/book_801_page_1.jpg")
                        imageUrl = "$baseId/$imageFileName",
                        texts = page.storyTexts,
                        pageNumber = page.pageNumber
                    )
                }
            )
        )
    }
}