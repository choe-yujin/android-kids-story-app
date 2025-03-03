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

class BookNetworkService @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun getMetadata(): GithubMetadata {
        return httpClient.get("https://raw.githubusercontent.com/choe-yujin/storybook-assets/master/metadata.json").body()
    }

    suspend fun downloadFile(url: String, outputFile: File): Boolean {
        return try {
            val response = httpClient.get(url)
            if (response.status.isSuccess()) {
                withContext(Dispatchers.IO) {
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