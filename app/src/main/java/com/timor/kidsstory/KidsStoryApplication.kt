package com.timor.kidsstory

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp

// 앱의 전역 상태와 의존성을 관리하는 Application 클래스
// Repository와 UseCase들의 의존성 주입을 담당
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