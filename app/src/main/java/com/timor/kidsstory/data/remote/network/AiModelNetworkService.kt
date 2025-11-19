package com.timor.kidsstory.data.remote.network

import com.orhanobut.logger.Logger
import com.timor.kidsstory.domain.util.ai.DownloadProgress
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

private const val DEFAULT_BUFFER_SIZE = 4096L

class AiModelNetworkService @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun downloadFile(url: String, outputFile: File): Flow<DownloadProgress> = flow {
        val response = httpClient.get(url)
        if (!response.status.isSuccess()) {
            throw Exception("Failed to download file: ${response.status}")
        }

        val totalBytes = response.headers["Content-Length"]?.toLong() ?: -1L
        var bytesCopied = 0L

        outputFile.parentFile?.mkdirs()
        val channel = response.bodyAsChannel()
        outputFile.outputStream().use { fileOut ->
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE)
                val bytes = packet.readBytes()
                fileOut.write(bytes)
                bytesCopied += bytes.size
                if (totalBytes > 0) {
                    val progress = (bytesCopied.toFloat() / totalBytes.toFloat())
                    emit(DownloadProgress.InProgress(progress))
                }
            }
        }
        if (bytesCopied > 0) {
            emit(DownloadProgress.Completed)
        }
    }.flowOn(Dispatchers.IO)
}