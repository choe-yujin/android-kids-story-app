package com.timor.kidsstory.di

import android.content.Context
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.data.repository.BookRepositoryImpl
import com.timor.kidsstory.domain.repository.BookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule { // Changed to object

    @Provides
    @Singleton
    fun provideBookRepository(
        assetDataSource: AssetDataSource,
        downloadedBooksDao: DownloadedBooksDao
    ): BookRepository {
        return BookRepositoryImpl(assetDataSource, downloadedBooksDao)
    }

    @Provides
    @Singleton
    fun provideBookDownloader(
        @ApplicationContext context: Context,
        networkService: BookNetworkService,
        downloadedBooksDao: DownloadedBooksDao,
        assetDataSource: AssetDataSource // Added assetDataSource
    ): BookDownloader {
        return BookDownloader(context, networkService, downloadedBooksDao, assetDataSource) // Pass assetDataSource
    }
}