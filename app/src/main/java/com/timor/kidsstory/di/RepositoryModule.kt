package com.timor.kidsstory.di

import android.content.Context
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
import com.timor.kidsstory.data.remote.BookDownloader
import com.timor.kidsstory.data.remote.network.BookNetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBookDownloader(
        @ApplicationContext context: Context,
        networkService: BookNetworkService,
        hybridBooksDao: com.timor.kidsstory.data.local.database.dao.HybridBooksDao, // Changed
        unifiedDataSource: UnifiedDataSource
    ): BookDownloader {
        return BookDownloader(context, networkService, hybridBooksDao, unifiedDataSource)
    }
}
