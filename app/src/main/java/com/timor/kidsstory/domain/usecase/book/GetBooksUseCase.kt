package com.timor.kidsstory.domain.usecase.book

import android.util.Log
import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.repository.BookRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(language: String = "en-ph"): Result<List<Book>> {
        Log.d("GetBooksUseCase", "Getting books with language: $language")

        // 언어 코드 처리
        val normalizedLanguage = when {
            language.startsWith("ko") -> "ko-kr"
            language.startsWith("tet") -> "tetum"
            else -> "en-ph"
        }

        return bookRepository.getBooks(normalizedLanguage)
    }
}