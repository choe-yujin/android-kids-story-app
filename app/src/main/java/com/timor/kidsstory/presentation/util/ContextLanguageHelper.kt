package com.timor.kidsstory.presentation.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

/**
 * 특정 언어의 Context를 생성하여 해당 언어의 string resource를 가져오는 유틸리티
 */
object ContextLanguageHelper {

    /**
     * 지정된 언어의 Context를 생성
     * @param context 기본 Context
     * @param languageCode 적용할 언어 코드 (예: "ko", "en", "tet", "mn")
     * @return 해당 언어가 적용된 Context
     */
    fun createLanguageContext(context: Context, languageCode: String): Context {
        val normalizedLanguageCode = normalizeLanguageCode(languageCode)
        val locale = Locale(normalizedLanguageCode)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            val resources = context.resources
            resources.updateConfiguration(configuration, resources.displayMetrics)
            context
        }
    }

    /**
     * 언어 코드를 정규화 (앱에서 지원하는 형태로 변환)
     * @param languageCode 원본 언어 코드
     * @return 정규화된 언어 코드
     */
    private fun normalizeLanguageCode(languageCode: String): String {
        return when {
            languageCode.contains("ko", ignoreCase = true) -> "ko"
            languageCode.contains("tet", ignoreCase = true) -> "tet"
            languageCode.contains("mn", ignoreCase = true) -> "mn"
            languageCode.contains("en", ignoreCase = true) -> "en"
            else -> "en" // 기본값
        }
    }

    /**
     * 지정된 언어로 string resource 가져오기
     * @param context 기본 Context
     * @param languageCode 언어 코드
     * @param stringRes string resource ID
     * @return 해당 언어의 문자열
     */
    fun getStringInLanguage(context: Context, languageCode: String, stringRes: Int): String {
        val languageContext = createLanguageContext(context, languageCode)
        return languageContext.getString(stringRes)
    }

    /**
     * 지정된 언어로 formatted string resource 가져오기
     * @param context 기본 Context
     * @param languageCode 언어 코드
     * @param stringRes string resource ID
     * @param formatArgs 포맷 인수들
     * @return 해당 언어의 포맷된 문자열
     */
    fun getStringInLanguage(context: Context, languageCode: String, stringRes: Int, vararg formatArgs: Any): String {
        val languageContext = createLanguageContext(context, languageCode)
        return languageContext.getString(stringRes, *formatArgs)
    }
}
