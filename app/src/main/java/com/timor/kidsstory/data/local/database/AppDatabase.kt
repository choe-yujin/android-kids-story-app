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
import com.timor.kidsstory.data.local.database.dao.UserDao
import com.timor.kidsstory.data.local.database.dao.UserBookInteractionDao
import com.timor.kidsstory.data.local.database.dao.AttendanceDao
import com.timor.kidsstory.data.local.database.dao.UnlockProgressDao
import com.timor.kidsstory.data.local.database.entity.AvailableBookEntity
import com.timor.kidsstory.data.local.database.entity.DownloadedBookEntity
import com.timor.kidsstory.data.local.database.entity.UserEntity
import com.timor.kidsstory.data.local.database.entity.UserBookInteractionEntity
import com.timor.kidsstory.data.local.database.entity.AttendanceEntity
import com.timor.kidsstory.data.local.database.entity.UnlockProgressEntity

@Database(
    entities = [
        DownloadedBookEntity::class, 
        AvailableBookEntity::class,
        UserEntity::class,
        UserBookInteractionEntity::class,
        AttendanceEntity::class,
        UnlockProgressEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun downloadedBooksDao(): DownloadedBooksDao
    abstract fun availableBooksDao(): AvailableBooksDao
    abstract fun userDao(): UserDao
    abstract fun userBookInteractionDao(): UserBookInteractionDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun unlockProgressDao(): UnlockProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE downloaded_books ADD COLUMN category TEXT NOT NULL DEFAULT ''")
            }
        }
        
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE downloaded_books ADD COLUMN level INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. users 테이블 생성
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        userId TEXT NOT NULL PRIMARY KEY,
                        username TEXT NOT NULL DEFAULT 'Guest',
                        userMode TEXT NOT NULL DEFAULT 'OFFLINE',
                        createdAt INTEGER NOT NULL,
                        lastActiveAt INTEGER NOT NULL
                    )
                """)
                
                // 2. 기본 사용자 추가
                database.execSQL("""
                    INSERT OR IGNORE INTO users 
                    (userId, username, userMode, createdAt, lastActiveAt) 
                    VALUES ('default_user', 'Guest', 'OFFLINE', ${System.currentTimeMillis()}, ${System.currentTimeMillis()})
                """)
                
                // 3. user_book_interactions 테이블 생성
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_book_interactions (
                        userId TEXT NOT NULL DEFAULT 'default_user',
                        bookId INTEGER NOT NULL,
                        language TEXT NOT NULL,
                        currentPage INTEGER NOT NULL DEFAULT 0,
                        totalPages INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER,
                        startedAt INTEGER,
                        lastReadAt INTEGER,
                        readCount INTEGER NOT NULL DEFAULT 0,
                        isBookmarked INTEGER NOT NULL DEFAULT 0,
                        bookmarkDate INTEGER,
                        PRIMARY KEY(userId, bookId, language),
                        FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE
                    )
                """)
                
                // 4. attendance 테이블 생성
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS attendance (
                        userId TEXT NOT NULL DEFAULT 'default_user',
                        date TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        streakCount INTEGER NOT NULL DEFAULT 1,
                        PRIMARY KEY(userId, date),
                        FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE
                    )
                """)
                
                // 5. unlock_progress 테이블 생성
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS unlock_progress (
                        userId TEXT NOT NULL DEFAULT 'default_user',
                        levelGroup TEXT NOT NULL,
                        language TEXT NOT NULL,
                        currentStep INTEGER NOT NULL DEFAULT 1,
                        lastUpdated INTEGER NOT NULL,
                        PRIMARY KEY(userId, levelGroup, language),
                        FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE
                    )
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "storybook_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    // TODO: 프로덕션 출시 전 반드시 제거 필요
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
