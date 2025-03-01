package com.timor.kidsstory.di

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.google.ai.client.generativeai.GenerativeModel
import com.timor.kidsstory.BuildConfig
import com.timor.kidsstory.data.local.assets.AssetDataSource
import com.timor.kidsstory.domain.util.TextToSpeechHelper
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
    fun provideAssetDataSource(@ApplicationContext context: Context): AssetDataSource {
        return AssetDataSource(context)
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
            modelName = "gemini-1.5-pro", apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideSpeechRecognizer(@ApplicationContext context: Context): SpeechRecognizer {
        return SpeechRecognizer.createSpeechRecognizer(context)
    }

    @Provides
    @Singleton
    fun provideRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

}