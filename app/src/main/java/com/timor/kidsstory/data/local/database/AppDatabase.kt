package com.timor.kidsstory.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timor.kidsstory.data.local.database.dao.AvailableBooksDao
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.local.database.entity.AvailableBookEntity
import com.timor.kidsstory.data.local.database.entity.DownloadedBookEntity

@Database(entities = [DownloadedBookEntity::class, AvailableBookEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadedBooksDao(): DownloadedBooksDao
    abstract fun availableBooksDao(): AvailableBooksDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "storybook_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}