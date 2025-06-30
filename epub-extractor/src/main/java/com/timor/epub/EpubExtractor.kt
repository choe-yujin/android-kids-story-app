package com.timor.epub

import com.timor.epub.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipInputStream

class EpubExtractor {
    
    fun extractEpub(
        epubFile: File,
        storyId: String,
        outputImagesDir: File,
        outputTranslationsDir: File
    ): StoryMetadata {
        val storyImagesDir = File(outputImagesDir, storyId)
        storyImagesDir.mkdirs()

        println("스토리 ID: $storyId, 이미지 디렉토리: ${storyImagesDir.absolutePath}")

        var imageCount = 0
        var coverImage = ""
        val pageTexts = mutableMapOf<Int, List<String>>()
        var credits = Credits()
        var epubMetadata = EpubMetadata()

        ZipInputStream(epubFile.inputStream()).use { zipIn ->
            var entry = zipIn.nextEntry

            while (entry != null) {
                val entryName = entry.name

                // 이미지 파일 처리
                if (isImageFile(entryName)) {
                    val imageFileName = File(entryName).name
                    val outputImageFile = File(storyImagesDir, imageFileName)
                    outputImageFile.outputStream().use { output ->
                        zipIn.copyTo(output)
                    }
                    imageCount++
                    if (imageFileName.contains("cover", ignoreCase = true) ||
                        imageFileName.contains("page_0", ignoreCase = true)) {
                        coverImage = imageFileName
                    }
                    println("  이미지 추출: $imageFileName")
                }

                // content.opf 메타데이터 파일 처리
                if (entryName.endsWith("content.opf") || entryName.endsWith(".opf")) {
                    val content = zipIn.bufferedReader().readText()
                    epubMetadata = extractEpubMetadata(content)
                    println("  EPUB 메타데이터 추출 완료")
                    println("    제목: ${epubMetadata.title ?: "없음"}")
                    println("    카테고리: ${epubMetadata.category ?: "없음"}")
                    println("    레벨: ${epubMetadata.level ?: "없음"}")
                }

                // XHTML 컨텐츠 파일 처리
                if (isContentFile(entryName)) {
                    val content = zipIn.bufferedReader().readText()

                    // 크레딧 페이지 처리
                    if (isCreditPage(entryName)) {
                        credits = extractCredits(content)
                        println("  저작자 정보 추출: ${credits.authors.size}명")
                    }

                    // 페이지 텍스트 추출
                    val pageNumber = extractPageNumber(entryName)
                    if (pageNumber != null) {
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

        // 번역 JSON 생성
        if (pageTexts.isNotEmpty()) {
            val sortedPages = pageTexts.entries.sortedBy { it.key }
                .map { (pageNumber, texts) -> Page(pageNumber, texts) }
            
            val translation = StoryTranslation(
                storyId, 
                epubMetadata.title ?: storyId, 
                sortedPages
            )
            
            saveTranslationJson(translation, File(outputTranslationsDir, "en"), storyId)
            saveTranslationJson(translation, File(outputTranslationsDir, "ko"), storyId)
            saveTranslationJson(translation, File(outputTranslationsDir, "tet"), storyId)
        }

        // 메타데이터 생성 - EPUB에서 추출한 정보 우선 사용
        return StoryMetadata(
            storyId = storyId,
            category = epubMetadata.category,
            level = epubMetadata.level,
            ageRange = epubMetadata.ageRange ?: "5-9",
            maker = credits.originalMaker ?: epubMetadata.publisher ?: "Unknown",
            region = epubMetadata.language,
            titles = mapOf(
                "en" to (epubMetadata.title ?: formatTitle(storyId)),
                "ko" to (epubMetadata.title ?: formatTitle(storyId)),
                "tet" to (epubMetadata.title ?: formatTitle(storyId))
            ),
            tags = epubMetadata.subjects.ifEmpty { listOf("story") },
            size = epubFile.length(),
            version = "1.0.0",
            imageCount = imageCount,
            pageCount = pageTexts.size,
            estimatedReadTime = pageTexts.size * 2,
            coverImage = coverImage
        )
    }

    /**
     * EPUB 메타데이터 추출
     */
    private fun extractEpubMetadata(content: String): EpubMetadata {
        var title: String? = null
        var category: String? = null
        var level: Int? = null
        var ageRange: String? = null
        var publisher: String? = null
        var language: String? = null
        val subjects = mutableListOf<String>()

        // 제목 추출
        val titlePattern = """<dc:title[^>]*>([^<]+)</dc:title>""".toRegex()
        titlePattern.find(content)?.let { match ->
            title = match.groupValues[1].trim()
        }

        // 출판사 추출
        val publisherPattern = """<dc:publisher[^>]*>([^<]+)</dc:publisher>""".toRegex()
        publisherPattern.find(content)?.let { match ->
            publisher = match.groupValues[1].trim()
        }

        // 언어 추출
        val languagePattern = """<dc:language[^>]*>([^<]+)</dc:language>""".toRegex()
        languagePattern.find(content)?.let { match ->
            language = match.groupValues[1].trim()
        }

        // 주제 추출
        val subjectPattern = """<dc:subject[^>]*>([^<]+)</dc:subject>""".toRegex()
        subjectPattern.findAll(content).forEach { match ->
            val subject = match.groupValues[1].trim()
            subjects.add(subject)
            
            // 주제에서 카테고리 파악
            when (subject.lowercase()) {
                "legend", "mythology", "myth" -> category = "LEGEND"
                "folktale", "fairy tale", "folk tale" -> category = "FOLKTALE"
                "culture", "festival", "cultural" -> category = "CULTURE"
                "daily life", "daily", "life" -> category = "DAILY_LIFE"
                "adventure" -> category = "ADVENTURE"
                "educational" -> category = "EDUCATIONAL"
            }
        }

        // 메타 태그에서 추가 정보 추출
        val metaPattern = """<meta\s+name="([^"]+)"\s+content="([^"]+)"""".toRegex()
        metaPattern.findAll(content).forEach { match ->
            val name = match.groupValues[1].lowercase()
            val metaContent = match.groupValues[2].trim()
            
            when (name) {
                "category", "genre" -> category = metaContent.uppercase()
                "reading.level", "level", "difficulty" -> level = metaContent.toIntOrNull()
                "age", "audience", "age.range" -> ageRange = metaContent
            }
        }

        return EpubMetadata(
            title = title,
            category = category,
            level = level,
            ageRange = ageRange,
            publisher = publisher,
            language = language,
            subjects = subjects
        )
    }

    /**
     * 저작자 정보 추출
     */
    private fun extractCredits(content: String): Credits {
        val authors = mutableListOf<AuthorInfo>()
        var originalMaker: String? = null
        var license: String? = null
        var copyright: String? = null

        // 한국어 패턴: **글** | 황티장 (Hoang Thi Trang)
        val koreanPattern = """\*\*([^*]+)\*\*\s*\|\s*([^<\n]+)""".toRegex()
        koreanPattern.findAll(content).forEach { match ->
            val role = match.groupValues[1].trim()
            val nameInfo = match.groupValues[2].trim()
            
            val authorInfo = parseNameInfo(role, nameInfo)
            authors.add(authorInfo)
        }

        // 라이선스 정보 추출
        val licensePattern = """is licensed under ([^<\n\.]+) by""".toRegex(RegexOption.IGNORE_CASE)
        licensePattern.find(content)?.let { match ->
            license = match.groupValues[1].trim()
        }

        // 저작권 정보 추출
        val copyrightPattern = """©\s*(\d{4})\s+by\s+([^<\n]+)""".toRegex()
        copyrightPattern.find(content)?.let { match ->
            val year = match.groupValues[1]
            val owner = match.groupValues[2].trim()
            copyright = "© $year by $owner"
            originalMaker = owner
        }

        // Enuma, Inc. 패턴 확인
        if (content.contains("Enuma, Inc", ignoreCase = true)) {
            originalMaker = "Enuma, Inc. & The Foundation SeeArt for Book Culture"
        }

        return Credits(
            authors = authors,
            originalMaker = originalMaker,
            license = license,
            copyright = copyright
        )
    }

    private fun parseNameInfo(role: String, nameInfo: String): AuthorInfo {
        val namePattern = """([^(]+)\s*\(([^)]+)\)""".toRegex()
        val match = namePattern.find(nameInfo)
        
        return if (match != null) {
            val koreanName = match.groupValues[1].trim()
            val originalName = match.groupValues[2].trim()
            AuthorInfo(
                role = mapRoleToEnglish(role),
                name = koreanName,
                originalName = originalName
            )
        } else {
            AuthorInfo(
                role = mapRoleToEnglish(role),
                name = nameInfo.trim()
            )
        }
    }

    private fun mapRoleToEnglish(koreanRole: String): String {
        return when (koreanRole.trim()) {
            "글" -> "Writer"
            "그림" -> "Illustrator"
            "편집" -> "Editor"
            "편집 디자인" -> "Design Editor"
            "베트남어 번역" -> "Vietnamese Translator"
            "중국어 번역" -> "Chinese Translator"
            "영어 번역" -> "English Translator"
            "태국어 번역" -> "Thai Translator"
            "목소리" -> "Voice Actor"
            else -> koreanRole
        }
    }

    private fun isCreditPage(filename: String): Boolean {
        val lower = filename.lowercase()
        return lower.contains("cover") || 
               lower.contains("credit") || 
               lower.contains("copyright")
    }

    // 기존 helper 메서드들
    private fun isImageFile(filename: String): Boolean {
        val lower = filename.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif")
    }

    private fun isContentFile(filename: String): Boolean {
        return filename.endsWith(".xhtml") || filename.endsWith(".html")
    }

    private fun extractPageNumber(filename: String): Int? {
        val pattern = """p(\d+)\.xhtml""".toRegex()
        val matchResult = pattern.find(filename)
        return matchResult?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun extractTexts(content: String): List<String> {
        val texts = mutableListOf<String>()

        val spanPattern = """<span id="f\d+">\s*(.*?)\s*</span>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        spanPattern.findAll(content).forEach { match ->
            val text = match.groupValues[1].trim()
            if (text.isNotEmpty()) {
                texts.add(text)
            }
        }

        val bracketPattern = """\[\s*(.*?)\s*\]\{#[^}]+\}""".toRegex()
        bracketPattern.findAll(content).forEach { match ->
            val text = match.groupValues[1].trim()
            if (text.isNotEmpty()) {
                texts.add(text)
            }
        }

        return texts
    }

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

        val outputFile = File(outputDir, "$storyId.json")
        outputFile.writeText(json.toString(2))
        println("  JSON 생성 완료: ${outputFile.absolutePath}")
    }

    fun saveMetadataJson(metadata: List<StoryMetadata>, outputFile: File) {
        outputFile.parentFile.mkdirs()

        val json = JSONObject().apply {
            put("stories", JSONArray().apply {
                metadata.forEach { story ->
                    put(JSONObject().apply {
                        put("storyId", story.storyId)
                        story.category?.let { put("category", it) }
                        story.level?.let { put("level", it) }
                        story.ageRange?.let { put("ageRange", it) }
                        story.maker?.let { put("maker", it) }
                        story.region?.let { put("region", it) }
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

        outputFile.writeText(json.toString(2))
        println("메타데이터 JSON 생성 완료: ${outputFile.absolutePath}")
    }
}