package com.timor.kidsstory

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.timor.kidsstory.di.UseCaseEntryPoint
import com.timor.kidsstory.domain.util.LocaleHelper.updateLanguage
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TaleTail 앱의 Application 클래스
 * - Hilt를 사용한 의존성 주입 설정
 * - 앱 시작 시점에 필요한 초기화 작업 수행
 * - WorkManager 초기화
 */
@HiltAndroidApp
class TaleTailApplication : Application(), Configuration.Provider {

    // WorkManager Factory 주입
    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    // Configuration.Provider 인터페이스 구현
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        initLogger()

//
//        val entryPoint = EntryPointAccessors.fromApplication(this, UseCaseEntryPoint::class.java)
//        val loadLanguageUseCase = entryPoint.getUserPreferenceUseCase()
//
//        CoroutineScope(Dispatchers.IO).launch {
//            loadLanguageUseCase().collect { userPref ->
//                Logger.e("저장된 언어 확인: $userPref")
//                updateLanguage(userPref.languageCode)
//            }
//        }
    }


    // Logger 초기화
    private fun initLogger() {
        // Logger 설정
        val strategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(5)
            .tag("TALETAIL_LOG")
            .build()

        Logger.clearLogAdapters()       // 로그 중복 출력 방지
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
    }
}
