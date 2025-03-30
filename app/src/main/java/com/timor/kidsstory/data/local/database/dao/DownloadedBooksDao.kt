package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timor.kidsstory.data.local.database.entity.DownloadedBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedBooksDao {
    @Query("SELECT * FROM downloaded_books WHERE language = :language ORDER BY downloadDate DESC")
    fun getDownloadedBooksByLanguage(language: String): Flow<List<DownloadedBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedBook(book: DownloadedBookEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM downloaded_books WHERE id = :bookId AND language = :language)")
    suspend fun isBookDownloaded(bookId: Int, language: String): Boolean

    @Query("SELECT * FROM downloaded_books WHERE id = :bookId AND language = :language")
    suspend fun getDownloadedBook(bookId: Int, language: String): DownloadedBookEntity?

    @Query("SELECT * FROM downloaded_books WHERE storyId LIKE :storyIdPrefix || '%'")
    suspend fun getDownloadedBooksByStoryId(storyIdPrefix: String): List<DownloadedBookEntity>
}