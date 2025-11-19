package com.timor.kidsstory.di

import com.timor.kidsstory.domain.util.ai.ModelDownloadManager
import com.timor.kidsstory.domain.util.ai.TtsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideTtsManager(modelDownloadManager: ModelDownloadManager): TtsManager {
        return TtsManager(modelDownloadManager)
    }
}
