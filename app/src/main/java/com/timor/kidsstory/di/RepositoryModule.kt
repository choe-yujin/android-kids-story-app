package com.timor.kidsstory.di

import android.content.Context
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.data.repository.BookRepositoryImpl
import com.timor.kidsstory.data.repository.UserPreferenceRepositoryImpl
import com.timor.kidsstory.domain.repository.BookRepository
import com.timor.kidsstory.domain.repository.UserPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
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

    companion object {
        @Provides
        @Singleton
        fun provideBookDownloader(
            @ApplicationContext context: Context,
            networkService: BookNetworkService,
            downloadedBooksDao: DownloadedBooksDao
        ): BookDownloader {
            return BookDownloader(context, networkService, downloadedBooksDao)
        }
    }
}