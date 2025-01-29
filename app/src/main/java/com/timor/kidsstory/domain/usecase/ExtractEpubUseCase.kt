package com.timor.kidsstory.domain.usecase

import android.content.Context
import android.util.Log
import com.timor.kidsstory.domain.model.StoryPage
import com.timor.kidsstory.domain.model.StoryResource
import com.timor.kidsstory.domain.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/*
epub 파일 파싱 및 리소스 추출 로직 담당
결과로 StoryResource 반환
*/

class ExtractEpubUseCase(
    private val context: Context,
    private val fileUtils: FileUtils = FileUtils(context)  // 기본값 제공
) {
    suspend operator fun invoke(epubFileName: String): Result<StoryResource> =
        withContext(Dispatchers.IO) {
            try {
                val storyId = fileUtils.sanitizeFileName(epubFileName.substringBeforeLast(".epub"))
                val storyDir = fileUtils.createStoryDirectories(storyId)
                val pageMap = processEpubFile(epubFileName, storyDir)

                val sortedPages = pageMap.entries
                    .sortedBy { it.key }
                    .map { it.value }

                Result.success(
                    StoryResource(
                        storyId = storyId,
                        title = storyId.replace("_", " "),
                        coverImage = sortedPages.firstOrNull()?.imageFileName ?: "",
                        pages = sortedPages
                    )
                )
            } catch (e: Exception) {
                Log.e("ExtractEpubUseCase", "Error extracting epub", e)
                Result.failure(e)
            }
        }

    private suspend fun processEpubFile(
        epubFileName: String,
        storyDir: File
    ): MutableMap<Int, StoryPage> {
        val pageMap = mutableMapOf<Int, StoryPage>()
        val imageDir = File(storyDir, "images")

        context.assets.open(epubFileName).use { inputStream ->
            fileUtils.processZipEntries(inputStream) { entryName, content ->
                when {
                    fileUtils.isTextFile(entryName) -> {
                        // ByteArray를 String으로 변환
                        val textContent = String(content, Charsets.UTF_8)
                        processTextEntry(entryName, textContent, pageMap)
                    }
                    fileUtils.isImageFile(entryName) -> {
                        processImageEntry(entryName, content, imageDir, pageMap)
                    }
                }
            }
        }
        return pageMap
    }

    private fun processTextEntry(
        entryName: String,
        content: String,
        pageMap: MutableMap<Int, StoryPage>
    ) {
        fileUtils.extractPageNumber(entryName)?.let { pageNumber ->
            val texts = fileUtils.extractTexts(content)
            val currentPage = pageMap.getOrPut(pageNumber) {
                StoryPage(pageNumber, "", emptyList())
            }
            pageMap[pageNumber] = currentPage.copy(storyTexts = texts)
        }
    }

    private fun processImageEntry(
        entryName: String,
        content: ByteArray,
        imageDir: File,
        pageMap: MutableMap<Int, StoryPage>
    ) {
        fileUtils.extractPageNumber(entryName)?.let { pageNumber ->
            val imageFileName = fileUtils.saveFile(content, imageDir, entryName)
            val currentPage = pageMap.getOrPut(pageNumber) {
                StoryPage(pageNumber, "", emptyList())
            }
            pageMap[pageNumber] = currentPage.copy(imageFileName = imageFileName)
        }
    }
}