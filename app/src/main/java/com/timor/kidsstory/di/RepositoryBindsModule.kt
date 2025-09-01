package com.timor.kidsstory.di

import com.timor.kidsstory.data.repository.UserPreferenceRepositoryImpl
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
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
}