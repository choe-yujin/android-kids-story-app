package com.timor.kidsstory.di

import com.timor.kidsstory.data.remote.network.BookNetworkService
import com.timor.kidsstory.data.remote.network.AppVersionNetworkService
import com.timor.kidsstory.data.remote.network.AppVersionNetworkServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
    }

    @Provides
    @Singleton
    fun provideBookNetworkService(httpClient: HttpClient): BookNetworkService {
        return BookNetworkService(httpClient)
    }

    @Provides
    @Singleton
    fun provideAppVersionNetworkService(httpClient: HttpClient): AppVersionNetworkService {
        return AppVersionNetworkServiceImpl(httpClient)
    }
}