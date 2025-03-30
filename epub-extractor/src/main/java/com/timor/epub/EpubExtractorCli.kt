package com.timor.epub

import com.timor.epub.model.StoryMetadata
import java.io.File

/**
 * 명령행 인터페이스(CLI) EPUB 추출 도구
 *
 * 이 도구를 사용하여 명령행에서 EPUB 파일을 일괄 처리할 수 있습니다.
 * 지정된 디렉토리의 모든 EPUB 파일을 처리하고, 이미지와 텍스트를 추출하여
 * 앱에서 사용 가능한 형식으로 변환합니다.
 *
 * 실행 예시:
 * java -jar epub-extractor.jar /path/to/epubs /path/to/output metadata.json
 */
object EpubExtractorCli {
    /**
     * 프로그램 진입점
     *
     * @param args 명령행 인자
     *             [0] EPUB 파일 디렉토리 경로
     *             [1] 출력 디렉토리 경로
     *             [2] 메타데이터 파일 경로
     */
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