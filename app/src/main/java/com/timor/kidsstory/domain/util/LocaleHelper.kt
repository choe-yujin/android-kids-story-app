package com.timor.kidsstory.domain.util

import android.content.Context
import com.timor.kidsstory.domain.util.LanguageConstants.getResourceLangCode
import java.util.Locale

/*
* 앱내에서 로케일 설정을 관리하는 헬퍼 클래스
* */
object LocaleHelper {

//    /*
//    * 제공된 언어코드에 따라 컨텍스트의 로케일 변경
//    *
//    * */
//
//    fun setLocale(context: Context, languageCode: String): Context {
//        // 언어 코드 정규화
//        val resourceCode = LanguageConstants.getResourceLangCode(languageCode)
//        Logger.e("언어 변경전 확인: $resourceCode")
//        return updateResources(context, resourceCode)
//    }
//
//
//    /*
//    * 리소스 설정을 업데이트 하여 새로운 로케일이 적용된 컨텍스트 반환
//    * */
//    private fun updateResources(context: Context, languageCode: String): Context {
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//
//        val configuration = Configuration(context.resources.configuration)
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            val localeList = LocaleList(locale)
//            LocaleList.setDefault(localeList)
//            configuration.setLocales(localeList)
//        } else {
//            configuration.locale = locale
//        }
//
//        return context.createConfigurationContext(configuration)
//    }

    fun Context.updateLanguage(languageCode: String) {
        val newLanguageCode = getResourceLangCode(languageCode)
        val locale = Locale(newLanguageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}