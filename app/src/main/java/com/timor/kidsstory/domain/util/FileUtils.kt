package com.timor.kidsstory.domain.util

import android.content.Context
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

class FileUtils(private val context: Context) {
    fun sanitizeFileName(fileName: String): String =
        fileName.replace(Regex("[^a-zA-Z0-9.-]"), "_")

    fun createStoryDirectories(storyId: String): File {
        return File(context.filesDir, storyId).apply {
            mkdirs()
            File(this, "images").mkdirs()
        }
    }

    fun isTextFile(fileName: String): Boolean {
        val pattern = """(?:^|/)p\d+\.xhtml$""".toRegex()
        return pattern.containsMatchIn(fileName)
    }

    fun isImageFile(fileName: String): Boolean {
        val lowerFileName = fileName.lowercase()
        return lowerFileName.contains("book_") &&
                lowerFileName.contains("page_") &&
                (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".png"))
    }

    fun extractPageNumber(fileName: String): Int? {
        // OEBPS/Text/p001.xhtml 형식도 처리
        val xhtmlPattern = """(?:^|/)p(\d+)\.xhtml$""".toRegex()
        xhtmlPattern.find(fileName)?.let { match ->
            return match.groupValues[1].toIntOrNull()
        }

        val imagePattern = """.*?(?:^|/)(?:book_\d+_)?page_(\d+)\.(jpg|png)$""".toRegex()
        imagePattern.find(fileName)?.let { match ->
            return match.groupValues[1].toIntOrNull()
        }

        Log.d("EpubExtractor", "Could not extract page number from: $fileName")
        return null
    }

    fun extractTexts(content: String): List<String> {
        try {
            Log.d("EpubExtractor", "Extracting texts from content")
            val texts = mutableListOf<String>()

            // 1. HTML div 형식 처리
            val divPattern =
                """<div id="divText"[^>]*>.*?</div>""".toRegex(RegexOption.DOT_MATCHES_ALL)
            divPattern.find(content)?.let { divMatch ->
                val divContent = divMatch.value
                Log.d("EpubExtractor", "Found HTML div: $divContent")

                // span 태그 내의 텍스트 추출
                val spanPattern = """<span id="f\d+">\s*(.*?)\s*</span>""".toRegex()
                spanPattern.findAll(divContent).forEach { spanMatch ->
                    val text = spanMatch.groupValues[1].trim()
                    if (text.isNotEmpty()) {
                        texts.add(text)
                        Log.d("EpubExtractor", "Added HTML text: $text")
                    }
                }
            }

            // 2. ::: 형식 처리
            if (texts.isEmpty()) {
                val colonPattern =
                    """:::\s*\{[^}]*divText[^}]*\}(.*?):::""".toRegex(RegexOption.DOT_MATCHES_ALL)
                colonPattern.find(content)?.let { colonMatch ->
                    val colonContent = colonMatch.groupValues[1]
                    Log.d("EpubExtractor", "Found colon format content: $colonContent")

                    // [ text ]{#id} 형식의 텍스트 추출
                    val bracketPattern = """\[\s*(.*?)\s*]\{#[^}]+\}""".toRegex()
                    bracketPattern.findAll(colonContent).forEach { bracketMatch ->
                        val text = bracketMatch.groupValues[1]
                            .replace("\n", " ")
                            .replace(Regex("\\s+"), " ")
                            .trim()
                        if (text.isNotEmpty()) {
                            texts.add(text)
                            Log.d("EpubExtractor", "Added colon format text: $text")
                        }
                    }
                }
            }

            Log.d("EpubExtractor", "Total texts extracted: ${texts.size}")
            texts.forEachIndexed { index, text ->
                Log.d("EpubExtractor", "Text $index: $text")
            }

            return texts
        } catch (e: Exception) {
            Log.e("EpubExtractor", "Error extracting texts", e)
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun processZipEntries(
        inputStream: InputStream,
        processor: suspend (String, ByteArray) -> Unit
    ) {
        ZipInputStream(inputStream).use { zipInputStream ->
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val entryName = entry.name
                    // ZIP 엔트리의 내용을 ByteArray로 읽기
                    val content = ByteArrayOutputStream().use { output ->
                        val buffer = ByteArray(4096)
                        var count: Int
                        while (zipInputStream.read(buffer).also { count = it } != -1) {
                            output.write(buffer, 0, count)
                        }
                        output.toByteArray()
                    }
                    processor(entryName, content)
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
        }
    }

    fun saveFile(content: ByteArray, directory: File, entryName: String): String {
        val fileName = sanitizeFileName(File(entryName).name)
        val outputFile = File(directory, fileName)

        FileOutputStream(outputFile).use { output ->
            output.write(content)
        }

        return fileName
    }

}