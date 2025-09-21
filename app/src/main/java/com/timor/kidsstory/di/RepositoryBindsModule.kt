package com.timor.kidsstory.di

import com.timor.kidsstory.data.repository.BookRepositoryImpl
import com.timor.kidsstory.data.repository.UserPreferenceRepositoryImpl
import com.timor.kidsstory.data.repository.AppVersionRepositoryImpl
import com.timor.kidsstory.data.repository.UpdateCheckRepositoryImpl
import com.timor.kidsstory.data.repository.AttendanceRepositoryImpl
import com.timor.kidsstory.data.repository.ReadingProgressRepositoryImpl
import com.timor.kidsstory.data.repository.leveltest.LevelTestRepositoryImpl
import com.timor.kidsstory.domain.repository.BookRepository
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import com.timor.kidsstory.domain.repository.AppVersionRepository
import com.timor.kidsstory.domain.repository.UpdateCheckRepository
import com.timor.kidsstory.domain.repository.AttendanceRepository
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import com.timor.kidsstory.domain.repository.leveltest.LevelTestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryBindsModule {
    
    @Binds
    @Singleton
    fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    @Binds
    @Singleton
    fun bindUserPreferenceRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository

    @Binds
    @Singleton
    fun bindAppVersionRepository(
        appVersionRepositoryImpl: AppVersionRepositoryImpl
    ): AppVersionRepository

    @Binds
    @Singleton
    fun bindUpdateCheckRepository(
        updateCheckRepositoryImpl: UpdateCheckRepositoryImpl
    ): UpdateCheckRepository
    
    // 새로 추가된 Repository들
    @Binds
    @Singleton
    fun bindAttendanceRepository(
        attendanceRepositoryImpl: AttendanceRepositoryImpl
    ): AttendanceRepository
    
    @Binds
    @Singleton
    fun bindReadingProgressRepository(
        readingProgressRepositoryImpl: ReadingProgressRepositoryImpl
    ): ReadingProgressRepository
    
    // Level Test Repository
    @Binds
    @Singleton
    fun bindLevelTestRepository(
        levelTestRepositoryImpl: LevelTestRepositoryImpl
    ): LevelTestRepository
}
