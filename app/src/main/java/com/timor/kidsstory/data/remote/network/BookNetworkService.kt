package com.timor.kidsstory.data.remote.network

import android.util.Log
import com.timor.kidsstory.data.remote.model.GithubMetadata
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

/**
 * 원격 서버에서 책 데이터를 다운로드하기 위한 네트워크 서비스
 * - 메타데이터 조회 및 파일 다운로드 기능 제공
 */

/**
 * 책 관련 네트워크 작업을 수행하는 서비스 클래스
 * - 원격 저장소에서 메타데이터 및 콘텐츠 파일 다운로드
 *
 * @property httpClient Ktor HTTP 클라이언트
 */
class BookNetworkService @Inject constructor(
    private val httpClient: HttpClient
) {

    /**
     * GitHub에서 메타데이터 정보 가져오기
     * - 앱 업데이트 및 새 콘텐츠 확인을 위한 메타데이터 조회
     *
     * @return 메타데이터 객체
     */
    suspend fun getMetadata(): GithubMetadata {
        return try {
            val response = httpClient.get("https://raw.githubusercontent.com/choe-yujin/storybook-assets/master/metadata.json")
            if (!response.status.isSuccess()) {
                Log.e("BookNetworkService", "메타데이터 가져오기 실패: ${response.status.value} ${response.status.description}")
                throw Exception("메타데이터 가져오기 실패: ${response.status.value} ${response.status.description}")
            }

            val jsonString = response.bodyAsText()
            Log.d("BookNetworkService", "메타데이터 JSON 수신: ${jsonString.take(200)}...") // Log first 200 chars
            Json.decodeFromString<GithubMetadata>(jsonString)
        } catch (e: Exception) {
            Log.e("BookNetworkService", "GitHub에서 메타데이터 가져오는 중 오류 발생: ${e.message}", e)
            throw e // Re-throw to be caught by use case
        }
    }

    /**
     * 원격 URL에서 파일 다운로드
     *
     * @param url 다운로드할 파일의 URL
     * @param outputFile 저장할 로컬 파일
     * @return 다운로드 성공 여부
     */
    suspend fun downloadFile(url: String, outputFile: File): Boolean {
        return try {
            val response = httpClient.get(url)
            if (response.status.isSuccess()) {
                // IO 작업은 Dispatchers.IO 컨텍스트에서 수행
                withContext(Dispatchers.IO) {
                    outputFile.parentFile?.mkdirs()
                    outputFile.outputStream().use { fileOut ->
                        response.bodyAsChannel().copyTo(fileOut)
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 🆕 파일 크기 확인 (HEAD 요청)
     */
    suspend fun getFileSize(url: String): Long {
        return try {
            val response = httpClient.get(url) {
                // HEAD 요청 대신 GET 요청의 header만 확인
            }
            if (response.status.isSuccess()) {
                val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: 0L
                Log.d("BookNetworkService", "📊 File size for $url: ${contentLength / 1024}KB")
                contentLength
            } else {
                Log.w("BookNetworkService", "Failed to get file size for $url: ${response.status}")
                0L
            }
        } catch (e: Exception) {
            Log.e("BookNetworkService", "Error getting file size for $url", e)
            0L
        }
    }
}