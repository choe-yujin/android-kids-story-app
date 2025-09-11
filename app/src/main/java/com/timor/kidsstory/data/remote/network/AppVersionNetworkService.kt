package com.timor.kidsstory.data.remote.network

import android.util.Log
import com.timor.kidsstory.data.dto.AppMetadataDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 앱 버전 정보를 가져오는 네트워크 서비스 인터페이스
 */
interface AppVersionNetworkService {
    suspend fun getAppMetadata(): Result<AppMetadataDto>
}

/**
 * 앱 버전 정보를 GitHub에서 가져오는 네트워크 서비스 구현체
 */
class AppVersionNetworkServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : AppVersionNetworkService {

    companion object {
        private const val APP_METADATA_URL = 
            "https://raw.githubusercontent.com/choe-yujin/storybook-assets/master/app_metadata.json"
    }

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    override suspend fun getAppMetadata(): Result<AppMetadataDto> {
        return try {
            Log.d("AppVersionNetworkService", "Fetching app metadata from: $APP_METADATA_URL")
            
            val response = httpClient.get(APP_METADATA_URL)
            val responseText = response.bodyAsText()
            
            Log.d("AppVersionNetworkService", "Response Content-Type: ${response.headers["Content-Type"]}")
            Log.d("AppVersionNetworkService", "Response body: $responseText")
            
            // Content-Type에 관계없이 JSON으로 파싱
            val metadata = json.decodeFromString<AppMetadataDto>(responseText)
            
            Log.d("AppVersionNetworkService", "Parsed metadata: $metadata")
            Result.success(metadata)
        } catch (e: Exception) {
            Log.e("AppVersionNetworkService", "Error fetching app metadata", e)
            Result.failure(e)
        }
    }
}