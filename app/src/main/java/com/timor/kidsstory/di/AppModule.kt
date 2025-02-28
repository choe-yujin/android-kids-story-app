package com.timor.kidsstory.di

import android.content.Context
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.data.mapper.BookMapper
import com.timor.kidsstory.data.mapper.PageMapper
import com.timor.kidsstory.domain.util.TextToSpeechHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAssetDataSource(@ApplicationContext context: Context): AssetDataSource {
        return AssetDataSource(context)
    }

    @Provides
    @Singleton
    fun provideBookMapper(): BookMapper {
        return BookMapper()
    }

    @Provides
    @Singleton
    fun providePageMapper(): PageMapper {
        return PageMapper()
    }

    @Provides
    @Singleton
    fun provideTextToSpeechHelper(@ApplicationContext context: Context): TextToSpeechHelper {
        return TextToSpeechHelper(context)
    }
}