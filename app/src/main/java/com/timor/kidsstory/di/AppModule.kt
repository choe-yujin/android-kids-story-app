package com.timor.kidsstory.di

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.google.ai.client.generativeai.GenerativeModel
import com.timor.kidsstory.BuildConfig
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.local.database.dao.HybridBooksDao
import com.timor.kidsstory.domain.manager.content.HybridContentManager
import com.timor.kidsstory.domain.service.ContentUpdateService
import com.timor.kidsstory.domain.util.TextToSpeechHelper
import com.timor.kidsstory.domain.util.SoundEffectManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUnifiedDataSource(
        @ApplicationContext context: Context,
        httpClient: io.ktor.client.HttpClient
    ): UnifiedDataSource {
        return UnifiedDataSource(context, httpClient)
    }

    @Provides
    @Singleton
    fun provideHybridContentManager(
        @ApplicationContext context: Context,
        hybridBooksDao: HybridBooksDao
    ): HybridContentManager {
        return HybridContentManager(context, hybridBooksDao)
    }

    @Provides
    @Singleton
    fun provideContentUpdateService(
        @ApplicationContext context: Context,
        hybridContentManager: HybridContentManager,
        networkService: com.timor.kidsstory.data.remote.network.BookNetworkService
    ): ContentUpdateService {
        return ContentUpdateService(context, hybridContentManager, networkService)
    }

    @Provides
    @Singleton
    fun provideTextToSpeechHelper(@ApplicationContext context: Context): TextToSpeechHelper {
        return TextToSpeechHelper(context)
    }

    @Provides
    @Singleton
    fun provideChatModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideSpeechRecognizer(@ApplicationContext context: Context): SpeechRecognizer? {
        return try {
            SpeechRecognizer.createSpeechRecognizer(context)
        } catch (e: Exception) {
            null
        }
    }

    @Provides
    @Singleton
    fun provideRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    @Provides
    @Singleton
    fun provideSoundEffectManager(@ApplicationContext context: Context): SoundEffectManager {
        return SoundEffectManager(context)
    }
}
