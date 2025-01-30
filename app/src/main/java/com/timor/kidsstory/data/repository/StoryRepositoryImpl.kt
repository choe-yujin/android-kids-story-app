package com.timor.kidsstory.data.repository

import android.content.Context
import com.timor.kidsstory.domain.model.Page
import com.timor.kidsstory.domain.model.StoryResource
import com.timor.kidsstory.domain.repository.StoryRepository
import com.timor.kidsstory.domain.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// StoryRepository 구현체, epub 파일 파싱 및 데이터 관리
class StoryRepositoryImpl(
    private val context: Context,
    private val fileUtils: FileUtils
) : StoryRepository {
    private val parsedStories = mutableMapOf<String, StoryResource>()

    override suspend fun getStories(): List<StoryResource> {
        // epub 파일들을 로드하고 파싱
        val epubFiles = context.assets.list("")
            ?.filter { it.endsWith(".epub", true) }
            ?: emptyList()

        epubFiles.forEach { epubFileName ->
            parseEpub(epubFileName)  // 각 epub 파일을 파싱하고 캐시
        }

        return parsedStories.values.toList()
    }

    override suspend fun parseEpub(epubFileName: String): Result<StoryResource> =
        withContext(Dispatchers.IO) {
            try {
                val storyId = fileUtils.sanitizeFileName(epubFileName.substringBeforeLast(".epub"))

                // 이미 파싱된 스토리가 있다면 반환
                parsedStories[storyId]?.let {
                    return@withContext Result.success(it)
                }

                val storyDir = fileUtils.createStoryDirectories(storyId)
                val pageMap = processEpubFile(epubFileName, storyDir)

                val sortedPages = pageMap.entries
                    .sortedBy { it.key }
                    .map { it.value }

                val story = StoryResource(
                    storyId = storyId,
                    title = storyId.replace("_", " "),
                    coverImage = sortedPages.firstOrNull()?.imageFileName ?: "",
                    pages = sortedPages
                )

                // 파싱된 스토리 캐싱
                parsedStories[storyId] = story

                Result.success(story)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun getStoryById(storyId: String): StoryResource? = parsedStories[storyId]

    private suspend fun processEpubFile(
        epubFileName: String,
        storyDir: File
    ): MutableMap<Int, Page> {
        val pageMap = mutableMapOf<Int, Page>()
        val imageDir = File(storyDir, "images")

        context.assets.open(epubFileName).use { inputStream ->
            fileUtils.processZipEntries(inputStream) { entryName, content ->
                when {
                    fileUtils.isTextFile(entryName) -> {
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
        pageMap: MutableMap<Int, Page>
    ) {
        fileUtils.extractPageNumber(entryName)?.let { pageNumber ->
            val texts = fileUtils.extractTexts(content)
            val currentPage = pageMap.getOrPut(pageNumber) {
                Page(pageNumber, "", emptyList())
            }
            pageMap[pageNumber] = currentPage.copy(storyTexts = texts)
        }
    }

    private fun processImageEntry(
        entryName: String,
        content: ByteArray,
        imageDir: File,
        pageMap: MutableMap<Int, Page>
    ) {
        fileUtils.extractPageNumber(entryName)?.let { pageNumber ->
            val imageFileName = fileUtils.saveFile(content, imageDir, entryName)
            val currentPage = pageMap.getOrPut(pageNumber) {
                Page(pageNumber, "", emptyList())
            }
            pageMap[pageNumber] = currentPage.copy(imageFileName = imageFileName)
        }
    }

}