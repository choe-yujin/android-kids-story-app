package com.timor.kidsstory.di

import com.timor.kidsstory.data.local.database.dao.UserDao
import com.timor.kidsstory.domain.manager.AttendanceManager
import com.timor.kidsstory.domain.manager.BookInteractionManager
import com.timor.kidsstory.domain.manager.FirstRunManager
import com.timor.kidsstory.domain.manager.UserManager
import com.timor.kidsstory.domain.manager.questionbank.QuestionBankManager
import com.timor.kidsstory.domain.repository.AttendanceRepository
import com.timor.kidsstory.domain.repository.ReadingProgressRepository
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Manager 클래스들을 위한 DI 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun provideUserManager(
        userDao: UserDao
    ): UserManager {
        return UserManager(userDao)
    }

    @Provides
    @Singleton
    fun provideAttendanceManager(
        attendanceRepository: AttendanceRepository
    ): AttendanceManager {
        return AttendanceManager(attendanceRepository)
    }

    @Provides
    @Singleton
    fun provideBookInteractionManager(
        readingProgressRepository: ReadingProgressRepository
    ): BookInteractionManager {
        return BookInteractionManager(readingProgressRepository)
    }
    
    @Provides
    @Singleton
    fun provideFirstRunManager(
        userPreferenceRepository: UserPreferenceRepository,
        questionBankManager: QuestionBankManager
    ): FirstRunManager {
        return FirstRunManager(userPreferenceRepository, questionBankManager)
    }
    
    // QuestionBankManager는 @Inject constructor를 사용하므로 자동으로 제공됨
}
