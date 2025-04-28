package com.timor.kidsstory.domain.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.orhanobut.logger.Logger
import java.util.Locale

/*
* 앱내에서 로케일 설정을 관리하는 헬퍼 클래스
* */
object LocaleHelper {
//    fun Context.updateLanguage(languageCode: String) {
//        val newLanguageCode = getResourceLangCode(languageCode)
//        val locale = Locale(newLanguageCode)
//        Locale.setDefault(locale)
//
//        val config = resources.configuration
//        config.setLocale(locale)
//        config.setLayoutDirection(locale)
//
//        resources.updateConfiguration(config, resources.displayMetrics)
//    }


    fun Context.updateLanguage(languageCode: String) {
        try {
            Logger.e("언어 코드 확인1: $languageCode")
            val newLanguageCode = when {
                languageCode.contains("en") -> "en"
                languageCode.contains("ko") -> "ko"
                languageCode.contains("tet") -> "tet"
                else -> "en"
            }

            Logger.e("언어 코드 확인2: $newLanguageCode")


            val locale = Locale(newLanguageCode)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeList = LocaleList(locale)
                (getSystemService(Context.LOCALE_SERVICE) as LocaleManager).applicationLocales = localeList
            } else {
                Locale.setDefault(locale)
                val resources = applicationContext.resources
                val configuration = resources.configuration
                configuration.setLocale(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
        } catch (e: Exception) {
            Logger.e("MainActivity", "updateLanguage: Error updating language $e")
        }
    }

    // 시스템 언어를 감지하고 앱에서 지원하는 언어 코드로 변환
    fun getSystemLanguageCode(context: Context): String {
        // API 24 기준으로 시스템 언어를 가져오는 방법 분기 처리
        val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }

        val languageCode = systemLocale.language
        return languageCode
    }
}