package com.timor.kidsstory.domain.repository

import com.timor.kidsstory.domain.model.Book
import com.timor.kidsstory.domain.model.Page

interface BookRepository {
    suspend fun getBooks(language: String): Result<List<Book>>
    suspend fun getBookById(storyId: String, language: String): Result<Book?>
    suspend fun getBookPages(storyId: String, language: String): Result<List<Page>>
}