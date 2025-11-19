package com.timor.kidsstory.di

import android.content.Context
import com.timor.kidsstory.data.local.database.AppDatabase
import com.timor.kidsstory.data.local.database.dao.AvailableBooksDao
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.data.local.database.dao.UserDao
import com.timor.kidsstory.data.local.database.dao.UserBookInteractionDao
import com.timor.kidsstory.data.local.database.dao.AttendanceDao
import com.timor.kidsstory.data.local.database.dao.ReadingProgressDao
import com.timor.kidsstory.data.local.database.dao.UnlockProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    // 기존 DAO들 (레거시 지원)
    @Provides
    fun provideDownloadedBooksDao(database: AppDatabase): DownloadedBooksDao {
        return database.downloadedBooksDao()
    }

    @Provides
    fun provideAvailableBooksDao(database: AppDatabase): AvailableBooksDao {
        return database.availableBooksDao()
    }

    // 새로운 하이브리드 DAO
    @Provides
    fun provideHybridBooksDao(database: AppDatabase): HybridBooksDao {
        return database.hybridBooksDao()
    }

    // 사용자 관련 DAO들
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideUserBookInteractionDao(database: AppDatabase): UserBookInteractionDao {
        return database.userBookInteractionDao()
    }

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    fun provideReadingProgressDao(database: AppDatabase): ReadingProgressDao {
        return database.readingProgressDao()
    }

    @Provides
    fun provideUnlockProgressDao(database: AppDatabase): UnlockProgressDao {
        return database.unlockProgressDao()
    }
}
