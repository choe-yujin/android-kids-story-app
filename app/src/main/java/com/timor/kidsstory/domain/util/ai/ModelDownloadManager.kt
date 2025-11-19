package com.timor.kidsstory.domain.util.ai

import android.content.Context
import com.orhanobut.logger.Logger
import com.timor.kidsstory.data.local.assets.UnifiedDataSource
import com.timor.kidsstory.data.remote.network.AiModelNetworkService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unifiedDataSource: UnifiedDataSource,
    private val aiModelNetworkService: AiModelNetworkService
) {
    private val modelsDir = File(context.filesDir, "models").apply {
        if (!exists()) mkdirs()
    }

    private suspend fun getModelFileNames(modelKey: String): Triple<String, String, String>? {
        val remoteMetadata = unifiedDataSource.loadRemoteBooksMetadata().getOrNull() ?: return null
        val aiModel = remoteMetadata.aiModels?.get(modelKey) ?: return null
        
        val glowTtsFileName = aiModel.glowTtsUrl.substringAfterLast('/')
        val hifiganFileName = aiModel.hifiganUrl.substringAfterLast('/')
        val configFileName = aiModel.configUrl?.substringAfterLast('/') ?: return null

        return Triple(glowTtsFileName, hifiganFileName, configFileName)
    }

    suspend fun isModelDownloaded(modelKey: String): Boolean {
        val fileNames = getModelFileNames(modelKey) ?: return false
        val fastSpeechFile = File(modelsDir, fileNames.first)
        val hifiGanFile = File(modelsDir, fileNames.second)
        val configFile = File(modelsDir, fileNames.third)
        
        return fastSpeechFile.exists() && hifiGanFile.exists() && configFile.exists() &&
               fastSpeechFile.length() > 0 && hifiGanFile.length() > 0 && configFile.length() > 0
    }

    suspend fun getModelPaths(modelKey: String): Triple<String, String, String>? {
        val fileNames = getModelFileNames(modelKey) ?: return null
        val fastSpeechFile = File(modelsDir, fileNames.first)
        val hifiGanFile = File(modelsDir, fileNames.second)
        val configFile = File(modelsDir, fileNames.third)

        return if (isModelDownloaded(modelKey)) {
            Triple(fastSpeechFile.absolutePath, hifiGanFile.absolutePath, configFile.absolutePath)
        } else {
            null
        }
    }

    fun downloadModels(modelKey: String): Flow<DownloadProgress> = channelFlow {
        try {
            Logger.d("ModelDownloadManager: GitHub에서 최신 메타데이터 확인 중...")
            val remoteMetadata = unifiedDataSource.loadRemoteBooksMetadata().getOrThrow()
            val aiModel = remoteMetadata.aiModels?.get(modelKey)
                ?: throw Exception("AI 모델 정보를 찾을 수 없습니다: $modelKey")

            val glowTtsUrl = aiModel.glowTtsUrl
            val hifiganUrl = aiModel.hifiganUrl
            val configUrl = aiModel.configUrl ?: throw Exception("Config URL이 없습니다.")

            if (glowTtsUrl.isBlank() || hifiganUrl.isBlank() || configUrl.isBlank()) {
                throw Exception("AI 모델 URL이 비어있습니다.")
            }

            Logger.d("ModelDownloadManager: 모델 다운로드 시작...")

            val filesToDownload = listOf(
                glowTtsUrl to File(modelsDir, glowTtsUrl.substringAfterLast('/')), 
                hifiganUrl to File(modelsDir, hifiganUrl.substringAfterLast('/')), 
                configUrl to File(modelsDir, configUrl.substringAfterLast('/'))
            )

            val progresses = MutableList(filesToDownload.size) { 0f }
            val completions = MutableList(filesToDownload.size) { false }

            filesToDownload.forEachIndexed { index, (url, file) ->
                launch {
                    aiModelNetworkService.downloadFile(url, file).collect {
                        when (it) {
                            is DownloadProgress.InProgress -> {
                                progresses[index] = it.progress
                                send(DownloadProgress.InProgress(progresses.average().toFloat()))
                            }
                            is DownloadProgress.Completed -> {
                                completions[index] = true
                                if (completions.all { it }) {
                                    send(DownloadProgress.Completed)
                                    close()
                                }
                            }
                            is DownloadProgress.Error -> {
                                send(it)
                                close()
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Logger.e("ModelDownloadManager: 모델 다운로드 중 오류 발생: ${e.message}", e)
            send(DownloadProgress.Error(e.message ?: "알 수 없는 오류"))
            close()
        }
    }

    suspend fun deleteModels(modelKey: String) {
        try {
            val fileNames = getModelFileNames(modelKey)
            fileNames?.let {
                File(modelsDir, it.first).delete()
                File(modelsDir, it.second).delete()
                File(modelsDir, it.third).delete()
                Logger.d("ModelDownloadManager: 모델 파일 삭제 완료")
            }
        } catch (e: Exception) {
            Logger.e("ModelDownloadManager: 모델 파일 삭제 실패: ${e.message}")
        }
    }
}

sealed class DownloadProgress {
    data class InProgress(val progress: Float) : DownloadProgress()
    object Completed : DownloadProgress()
    data class Error(val message: String) : DownloadProgress()
}