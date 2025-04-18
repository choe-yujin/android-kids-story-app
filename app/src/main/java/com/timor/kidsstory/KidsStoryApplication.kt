package com.timor.kidsstory

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp

/**
 * 앱의 Application 클래스
 * - Hilt를 사용한 의존성 주입 설정
 * - 앱 시작 시점에 필요한 초기화 작업 수행
 */
@HiltAndroidApp
class KidsStoryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initLogger()
    }

    // Logger 초기화
    private fun initLogger() {
        // Logger 설정
        val strategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(5)
            .tag("LOG_RESULT")
            .build()

        Logger.clearLogAdapters()       // 로그 중복 출력 방지
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
    }
}