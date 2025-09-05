package com.timor.kidsstory.data.remote.network

import com.timor.kidsstory.data.dto.AppMetadataDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
            "https://raw.githubusercontent.com/choe-yujin/android-kids-story-app/main/app_metadata.json"
    }

    override suspend fun getAppMetadata(): Result<AppMetadataDto> {
        return try {
            val response = httpClient.get(APP_METADATA_URL)
            val metadata = response.body<AppMetadataDto>()
            Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}