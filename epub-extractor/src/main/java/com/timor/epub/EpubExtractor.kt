package com.timor.epub

import com.timor.epub.model.Page
import com.timor.epub.model.StoryMetadata
import com.timor.epub.model.StoryTranslation
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipInputStream

/**
 * EPUB 파일 추출기 클래스
 *
 * EPUB 전자책 파일에서 텍스트와 이미지를 추출하고 앱에서 사용 가능한 형태로 변환합니다.
 * 주요 기능:
 * - EPUB(ZIP 형식) 파일에서 이미지 및 텍스트 콘텐츠 추출
 * - 페이지별 텍스트를 구조화된 데이터로 변환
 * - 다국어 번역 JSON 파일 생성 (영어, 한국어, 테툼어)
 * - 도서 메타데이터 자동 생성
 */
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
     *
     * 추출된 페이지 텍스트를 JSON 형식으로 변환하여 언어별 파일로 저장합니다.
     *
     * @param translation 저장할 번역 객체
     * @param outputDir 출력 디렉토리
     * @param storyId 스토리 ID
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
     *
     * 모든 스토리의 메타데이터를 통합하여 하나의 JSON 파일로 저장합니다.
     *
     * @param metadata 저장할 메타데이터 목록
     * @param outputFile 출력 파일
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

    /**
     * 파일명이 이미지 파일인지 확인
     *
     * @param filename 확인할 파일명
     * @return 이미지 파일 여부
     */
    private fun isImageFile(filename: String): Boolean {
        val lower = filename.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif")
    }

    /**
     * 파일명이 HTML/XHTML 콘텐츠 파일인지 확인
     *
     * @param filename 확인할 파일명
     * @return 콘텐츠 파일 여부
     */
    private fun isContentFile(filename: String): Boolean {
        return filename.endsWith(".xhtml") || filename.endsWith(".html")
    }

    /**
     * 파일명에서 페이지 번호 추출
     *
     * "p001.xhtml"과 같은 파일명에서 페이지 번호(1)를 추출합니다.
     *
     * @param filename 파일명
     * @return 추출된 페이지 번호 또는 null
     */
    private fun extractPageNumber(filename: String): Int? {
        val pattern = """p(\d+)\.xhtml""".toRegex()
        val matchResult = pattern.find(filename)
        return matchResult?.groupValues?.get(1)?.toIntOrNull()
    }

    /**
     * HTML 콘텐츠에서 텍스트 추출
     *
     * HTML 마크업에서 span 태그 또는 마크다운 형식의 텍스트를 추출합니다.
     *
     * @param content HTML 콘텐츠
     * @return 추출된 텍스트 목록
     */
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

    /**
     * 스토리 ID를 제목 형식으로 변환
     *
     * "my_story_title"과 같은 ID를 "My Story Title"과 같은 형식으로 변환합니다.
     *
     * @param storyId 스토리 ID
     * @return 포맷된 제목
     */
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

    /**
     * 스토리 ID 기반으로 카테고리 추측
     *
     * 스토리 ID에 포함된 키워드를 기반으로 카테고리를 결정합니다.
     *
     * @param storyId 스토리 ID
     * @return 추측된 카테고리
     */
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

    /**
     * 페이지 수와 텍스트 양에 따른 난이도 추측
     *
     * 페이지 수와 텍스트의 총량에 따라 1-5 단계의 난이도를 결정합니다.
     *
     * @param pageCount 페이지 수
     * @param textCount 텍스트 수
     * @return 추측된 난이도 (1-5)
     */
    private fun guessLevel(pageCount: Int, textCount: Int): Int {
        return when {
            pageCount < 10 && textCount < 30 -> 1
            pageCount < 15 && textCount < 60 -> 2
            pageCount < 20 && textCount < 100 -> 3
            pageCount < 25 && textCount < 150 -> 4
            else -> 5
        }
    }

    /**
     * 스토리 ID에서 지역 추측
     *
     * 스토리 ID에 포함된 키워드를 기반으로 지역(국가)를 결정합니다.
     *
     * @param storyId 스토리 ID
     * @return 추측된 지역
     */
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