package com.timor.kidsstory.domain.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.orhanobut.logger.Logger
import java.util.Locale

/**
 * 앱 내에서 로케일 설정을 관리하는 헬퍼 클래스
 */
object LocaleHelper {

    /**
     * 앱의 언어를 강제로 변경
     */
    fun Context.updateLanguage(languageCode: String) {
        try {
            Logger.e("언어 코드 변경 시작: $languageCode")
            val newLanguageCode = normalizeLanguageCode(languageCode)
            Logger.e("정규화된 언어 코드: $newLanguageCode")

            val locale = Locale(newLanguageCode)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 이상: LocaleManager 사용
                val localeList = LocaleList(locale)
                (getSystemService(Context.LOCALE_SERVICE) as LocaleManager).applicationLocales = localeList
                Logger.e("Android 13+ LocaleManager 사용")
            } else {
                // Android 12 이하: 기존 방식
                Locale.setDefault(locale)
                val resources = applicationContext.resources
                val configuration = resources.configuration
                configuration.setLocale(locale)
                configuration.setLayoutDirection(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)
                Logger.e("Android 12- Configuration 업데이트")
            }
        } catch (e: Exception) {
            Logger.e("언어 업데이트 오류: $e")
        }
    }

    /**
     * 시스템 언어를 감지하고 앱에서 지원하는 언어 코드로 변환
     */
    fun getSystemLanguageCode(context: Context): String {
        val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }

        val systemLanguageCode = systemLocale.language
        val normalizedCode = normalizeLanguageCode(systemLanguageCode)
        
        Logger.e("시스템 언어: $systemLanguageCode -> 정규화: $normalizedCode")
        return normalizedCode
    }

    /**
     * 언어 코드를 앱에서 지원하는 형태로 정규화
     */
    private fun normalizeLanguageCode(languageCode: String): String {
        return when {
            languageCode.contains("ko", ignoreCase = true) -> "ko"
            languageCode.contains("tet", ignoreCase = true) -> "tet" 
            languageCode.contains("mn", ignoreCase = true) -> "mn"
            languageCode.contains("en", ignoreCase = true) -> "en"
            else -> "en" // 기본값은 영어
        }
    }

    /**
     * 현재 앱에 설정된 언어 코드 반환
     */
    fun getCurrentLanguageCode(context: Context): String {
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return normalizeLanguageCode(currentLocale.language)
    }

    /**
     * 앱이 지원하는 언어인지 확인
     */
    fun isSupportedLanguage(languageCode: String): Boolean {
        val normalized = normalizeLanguageCode(languageCode)
        return normalized in listOf("ko", "en", "tet", "mn")
    }
}
