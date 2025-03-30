package com.timor.kidsstory.data.remote.network

import com.timor.kidsstory.data.remote.model.GithubMetadata
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        return httpClient.get("https://raw.githubusercontent.com/choe-yujin/storybook-assets/master/metadata.json").body()
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
}