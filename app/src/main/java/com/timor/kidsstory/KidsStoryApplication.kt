package com.timor.kidsstory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// 앱의 전역 상태와 의존성을 관리하는 Application 클래스
// Repository와 UseCase들의 의존성 주입을 담당
@HiltAndroidApp
class KidsStoryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}