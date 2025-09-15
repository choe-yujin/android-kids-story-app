package com.timor.kidsstory.di

import com.timor.kidsstory.data.local.database.dao.AttendanceDao
import com.timor.kidsstory.data.local.database.dao.UserBookInteractionDao
import com.timor.kidsstory.data.local.database.dao.UserDao
import com.timor.kidsstory.domain.manager.AttendanceManager
import com.timor.kidsstory.domain.manager.BookInteractionManager
import com.timor.kidsstory.domain.manager.UserManager
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
        attendanceDao: AttendanceDao,
        userManager: UserManager
    ): AttendanceManager {
        return AttendanceManager(attendanceDao, userManager)
    }

    @Provides
    @Singleton
    fun provideBookInteractionManager(
        userBookInteractionDao: UserBookInteractionDao,
        userManager: UserManager
    ): BookInteractionManager {
        return BookInteractionManager(userBookInteractionDao, userManager)
    }
}
