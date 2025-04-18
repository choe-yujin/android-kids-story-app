package com.timor.kidsstory.di

import com.timor.kidsstory.data.repository.BookRepositoryImpl
import com.timor.kidsstory.data.repository.UserPreferenceRepositoryImpl
import com.timor.kidsstory.domain.repository.BookRepository
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferenceRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository
}