package com.timor.kidsstory.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timor.kidsstory.data.local.database.entity.AvailableBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AvailableBooksDao {
    @Query("SELECT * FROM available_books")
    fun getAllAvailableBooks(): Flow<List<AvailableBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailableBooks(books: List<AvailableBookEntity>)

    @Query("DELETE FROM available_books")
    suspend fun clearAvailableBooks()
}