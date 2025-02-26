package com.timor.epub

import com.timor.epub.model.StoryMetadata
import java.io.File

/**
 * 명령행에서 실행할 수 있는 EPUB 추출 도구
 */
object EpubExtractorCli {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 3) {
            println("Usage: EpubExtractorCli <epub-dir> <output-dir> <metadata-file>")
            return
        }

        val epubDir = File(args[0])
        val outputDir = File(args[1])
        val metadataFile = File(args[2])

        if (!epubDir.exists() || !epubDir.isDirectory) {
            println("Error: EPUB directory does not exist: ${epubDir.absolutePath}")
            return
        }

        // 출력 디렉토리 생성
        val imagesDir = File(outputDir, "images")
        val translationsDir = File(outputDir, "translations")

        imagesDir.mkdirs()
        translationsDir.mkdirs()

        // 언어별 디렉토리 생성
        File(translationsDir, "en").mkdirs()
        File(translationsDir, "ko").mkdirs()
        File(translationsDir, "tet").mkdirs()

        // EPUB 파일 처리
        val extractor = EpubExtractor()
        val metadata = mutableListOf<StoryMetadata>()

        val epubFiles = epubDir.listFiles { file -> file.name.endsWith(".epub", ignoreCase = true) }
            ?: emptyArray()

        println("Found ${epubFiles.size} EPUB files in ${epubDir.absolutePath}")

        epubFiles.forEach { epubFile ->
            println("Processing: ${epubFile.name}")

            val storyId = epubFile.nameWithoutExtension
            val storyMetadata = extractor.extractEpub(
                epubFile,
                storyId,
                imagesDir,
                translationsDir
            )

            metadata.add(storyMetadata)
        }

        // 메타데이터 저장
        extractor.saveMetadataJson(metadata, metadataFile)

        println("All EPUBs processed successfully!")
    }
}