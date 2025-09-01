package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 모든 책 목록을 가져오는 유스케이스
 * - 언어 코드 정규화 및 저장소 접근을 처리
 *
 * @property bookRepository 책 데이터 저장소
 */
class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    /**
     * 지정된 언어로 모든 책 목록을 가져옴
     *
     * @param language 언어 코드 (기본값: "en-ph")
     * @return 책 목록 또는 오류
     */
    suspend operator fun invoke(language: String = "en-ph"): Result<List<Book>> {
        Log.d("GetBooksUseCase", "Getting books with language: $language")

        // 언어 코드 정규화
        val normalizedLanguage = when {
            language.startsWith("ko") -> "ko-kr"
            language.startsWith("tet") -> "tetum"
            language.startsWith("mn") -> "mn-MN" // Added for Mongolian
            else -> "en-ph"
        }

        return bookRepository.getBooks(normalizedLanguage)
    }
}