package com.timor.kidsstory.di

import android.content.Context
import androidx.room.Room
import com.timor.kidsstory.data.local.database.AppDatabase
import com.timor.kidsstory.data.local.database.dao.AvailableBooksDao
import com.timor.kidsstory.data.local.database.dao.DownloadedBooksDao
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
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "storybook_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDownloadedBooksDao(database: AppDatabase): DownloadedBooksDao {
        return database.downloadedBooksDao()
    }

    @Provides
    fun provideAvailableBooksDao(database: AppDatabase): AvailableBooksDao {
        return database.availableBooksDao()
    }
}