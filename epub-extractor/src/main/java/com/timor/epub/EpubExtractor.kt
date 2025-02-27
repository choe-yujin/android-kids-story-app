package com.timor.epub

import com.timor.epub.model.Page
import com.timor.epub.model.StoryMetadata
import com.timor.epub.model.StoryTranslation
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipInputStream

class EpubExtractor {
    /**
     * EPUB 파일 처리 및 리소스 추출
     *
     * @param epubFile EPUB 파일
     * @param storyId 스토리 ID
     * @param outputImagesDir 이미지 출력 디렉토리
     * @param outputTranslationsDir 번역 출력 디렉토리
     * @return 스토리 메타데이터
     */
    fun extractEpub(
        epubFile: File,
        storyId: String,
        outputImagesDir: File,
        outputTranslationsDir: File
    ): StoryMetadata {
        // 스토리별 이미지 디렉토리 생성
        val storyImagesDir = File(outputImagesDir, storyId)
        storyImagesDir.mkdirs()

        println("스토리 ID: $storyId, 이미지 디렉토리: ${storyImagesDir.absolutePath}")

        var imageCount = 0
        var coverImage = ""

        // EPUB 파일(ZIP)에서 콘텐츠 추출
        val pageTexts = mutableMapOf<Int, List<String>>()

        ZipInputStream(epubFile.inputStream()).use { zipIn ->
            var entry = zipIn.nextEntry

            while (entry != null) {
                val entryName = entry.name

                // 이미지 파일 처리
                if (isImageFile(entryName)) {
                    val imageFileName = File(entryName).name
                    val outputImageFile = File(storyImagesDir, imageFileName)

                    // 이미지 파일 저장
                    outputImageFile.outputStream().use { output ->
                        zipIn.copyTo(output)
                    }

                    imageCount++

                    // 표지 이미지 확인
                    if (imageFileName.contains("cover", ignoreCase = true) ||
                        imageFileName.contains("page_0", ignoreCase = true)) {
                        coverImage = imageFileName
                    }

                    println("  이미지 추출: $imageFileName")
                }

                // XHTML 컨텐츠 파일 처리
                if (isContentFile(entryName)) {
                    val content = zipIn.bufferedReader().readText()

                    // 페이지 번호 추출
                    val pageNumber = extractPageNumber(entryName)
                    if (pageNumber != null) {
                        // 텍스트 추출
                        val texts = extractTexts(content)

                        if (texts.isNotEmpty()) {
                            pageTexts[pageNumber] = texts
                            println("  텍스트 추출: 페이지 $pageNumber (${texts.size} 텍스트)")
                        }
                    }
                }

                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }

        // 언어별 JSON 생성 및 저장
        if (pageTexts.isNotEmpty()) {
            val sortedPages = pageTexts.entries.sortedBy { it.key }
                .map { (pageNumber, texts) -> Page(pageNumber, texts) }

            val translation = StoryTranslation(storyId, formatTitle(storyId), sortedPages)

            // 영어 버전 저장
            saveTranslationJson(translation, File(outputTranslationsDir, "en"), storyId)

            // 임시로 다른 언어 버전도 동일하게 저장
            saveTranslationJson(translation, File(outputTranslationsDir, "ko"), storyId)
            saveTranslationJson(translation, File(outputTranslationsDir, "tet"), storyId)
        }

        // 카테고리와 난이도 추정
        val category = guessCategory(storyId)
        val level = guessLevel(pageTexts.size, pageTexts.values.sumOf { it.size })

        // 메타데이터 생성 및 반환
        return StoryMetadata(
            storyId = storyId,
            category = category,
            level = level,
            ageRange = "5-9",
            maker = "Enuma, Inc. & The Foundation SeeArt for Book Culture",
            region = guessRegion(storyId),
            titles = mapOf(
                "en" to formatTitle(storyId),
                "ko" to formatTitle(storyId),
                "tet" to formatTitle(storyId)
            ),
            tags = listOf("story", category.lowercase()),
            size = epubFile.length(),
            version = "1.0.0",
            imageCount = imageCount,
            pageCount = pageTexts.size,
            estimatedReadTime = pageTexts.size * 2,
            coverImage = coverImage
        )
    }

    /**
     * 번역 JSON 저장
     */
    private fun saveTranslationJson(translation: StoryTranslation, outputDir: File, storyId: String) {
        outputDir.mkdirs()

        val json = JSONObject().apply {
            put("storyId", translation.storyId)
            put("title", translation.title)
            put("pages", JSONArray().apply {
                translation.pages.forEach { page ->
                    put(JSONObject().apply {
                        put("pageNumber", page.pageNumber)
                        put("texts", JSONArray().apply {
                            page.texts.forEach { text ->
                                put(text)
                            }
                        })
                    })
                }
            })
        }

        // JSON 파일 저장
        val outputFile = File(outputDir, "$storyId.json")
        outputFile.writeText(json.toString(2))

        println("  JSON 생성 완료: ${outputFile.absolutePath}")
    }

    /**
     * 메타데이터 JSON 저장
     */
    fun saveMetadataJson(metadata: List<StoryMetadata>, outputFile: File) {
        outputFile.parentFile.mkdirs()

        val json = JSONObject().apply {
            put("stories", JSONArray().apply {
                metadata.forEach { story ->
                    put(JSONObject().apply {
                        put("storyId", story.storyId)
                        put("category", story.category)
                        put("level", story.level)
                        put("ageRange", story.ageRange)
                        put("maker", story.maker)
                        put("region", story.region)
                        put("titles", JSONObject().apply {
                            story.titles.forEach { (lang, title) ->
                                put(lang, title)
                            }
                        })
                        put("tags", JSONArray(story.tags))
                        put("size", story.size)
                        put("version", story.version)
                        put("imageCount", story.imageCount)
                        put("pageCount", story.pageCount)
                        put("estimatedReadTime", story.estimatedReadTime)
                        put("coverImage", story.coverImage)
                    })
                }
            })
        }

        // JSON 파일 저장
        outputFile.writeText(json.toString(2))

        println("메타데이터 JSON 생성 완료: ${outputFile.absolutePath}")
    }

    // 이미지 파일인지 확인
    private fun isImageFile(filename: String): Boolean {
        val lower = filename.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif")
    }

    // 컨텐츠 파일인지 확인
    private fun isContentFile(filename: String): Boolean {
        return filename.endsWith(".xhtml") || filename.endsWith(".html")
    }

    // 페이지 번호 추출
    private fun extractPageNumber(filename: String): Int? {
        val pattern = """p(\d+)\.xhtml""".toRegex()
        val matchResult = pattern.find(filename)
        return matchResult?.groupValues?.get(1)?.toIntOrNull()
    }

    // 텍스트 추출
    private fun extractTexts(content: String): List<String> {
        val texts = mutableListOf<String>()

        // <span id="f001">텍스트</span> 패턴 찾기
        val spanPattern = """<span id="f\d+">\s*(.*?)\s*</span>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        spanPattern.findAll(content).forEach { match ->
            val text = match.groupValues[1].trim()
            if (text.isNotEmpty()) {
                texts.add(text)
            }
        }

        // [ text ]{#id} 패턴 찾기 (마크다운 형식)
        val bracketPattern = """\[\s*(.*?)\s*\]\{#[^}]+\}""".toRegex()
        bracketPattern.findAll(content).forEach { match ->
            val text = match.groupValues[1].trim()
            if (text.isNotEmpty()) {
                texts.add(text)
            }
        }

        return texts
    }

    // 스토리 ID에서 제목 형식화
    private fun formatTitle(storyId: String): String {
        return storyId.replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                if (word.lowercase() in listOf("of", "the", "and", "in", "on", "at", "to", "for", "with", "by")) {
                    word.lowercase()
                } else {
                    word.replaceFirstChar { it.uppercase() }
                }
            }
    }

    // 스토리 ID 기반으로 카테고리 추측
    private fun guessCategory(storyId: String): String {
        return when {
            storyId.contains("legend", ignoreCase = true) -> "LEGEND"
            storyId.contains("story", ignoreCase = true) -> "FOLKTALE"
            storyId.contains("chick", ignoreCase = true) -> "FOLKTALE"
            storyId.contains("snake", ignoreCase = true) -> "LEGEND"
            storyId.contains("fish", ignoreCase = true) -> "FOLKTALE"
            storyId.contains("festival", ignoreCase = true) -> "CULTURE"
            storyId.contains("day", ignoreCase = true) -> "DAILY_LIFE"
            storyId.contains("dumpling", ignoreCase = true) -> "CULTURE"
            else -> "FOLKTALE" // 기본값
        }
    }

    // 페이지 수와 텍스트 양에 따른 난이도 추측
    private fun guessLevel(pageCount: Int, textCount: Int): Int {
        return when {
            pageCount < 10 && textCount < 30 -> 1
            pageCount < 15 && textCount < 60 -> 2
            pageCount < 20 && textCount < 100 -> 3
            pageCount < 25 && textCount < 150 -> 4
            else -> 5
        }
    }

    // 스토리 ID에서 지역 추측
    private fun guessRegion(storyId: String): String {
        return when {
            storyId.contains("Chinese", ignoreCase = true) ||
                    storyId.contains("Chang'e", ignoreCase = true) ||
                    storyId.contains("White Snake", ignoreCase = true) -> "China"

            storyId.contains("Philippines", ignoreCase = true) -> "Philippines"

            storyId.contains("Hoang", ignoreCase = true) ||
                    storyId.contains("Lac Long", ignoreCase = true) -> "Vietnam"

            storyId.contains("Krathong", ignoreCase = true) ||
                    storyId.contains("Chick Stars", ignoreCase = true) ||
                    storyId.contains("Golden Goby", ignoreCase = true) -> "Thailand"

            else -> "Asia" // 기본값
        }
    }
}