package com.timor.kidsstory.di

import com.timor.kidsstory.data.repository.UserPreferenceRepositoryImpl
import com.timor.kidsstory.data.repository.AppVersionRepositoryImpl
import com.timor.kidsstory.data.repository.UpdateCheckRepositoryImpl
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import com.timor.kidsstory.domain.repository.AppVersionRepository
import com.timor.kidsstory.domain.repository.UpdateCheckRepository
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
}