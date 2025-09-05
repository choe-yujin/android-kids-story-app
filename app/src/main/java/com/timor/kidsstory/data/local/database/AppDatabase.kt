package com.timor.kidsstory.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.timor.kidsstory.data.local.database.dao.AvailableBooksDao
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.local.database.entity.AvailableBookEntity
import com.timor.kidsstory.data.local.database.entity.DownloadedBookEntity

@Database(
    entities = [DownloadedBookEntity::class, AvailableBookEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadedBooksDao(): DownloadedBooksDao
    abstract fun availableBooksDao(): AvailableBooksDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 버전 2에서 버전 3으로의 마이그레이션 추가
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // DownloadedBookEntity에 category 컬럼 추가
                database.execSQL("ALTER TABLE downloaded_books ADD COLUMN category TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "storybook_database"
                )
                    // 버전 2에서 3으로 마이그레이션을 추가
                    .addMigrations(MIGRATION_2_3)
                    // 다른 버전 변경에 대해서는 fallbackToDestructiveMigration 유지
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}