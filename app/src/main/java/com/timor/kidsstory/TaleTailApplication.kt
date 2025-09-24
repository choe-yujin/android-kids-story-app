package com.timor.kidsstory

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.timor.kidsstory.di.UseCaseEntryPoint
import com.timor.kidsstory.domain.manager.UserManager
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
 * - UserManager 초기화 (확장 가능한 사용자 시스템)
 */
@HiltAndroidApp
class TaleTailApplication : Application(), Configuration.Provider {

    // WorkManager Factory 주입
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // UserManager 주입
    @Inject
    lateinit var userManager: UserManager

    // Configuration.Provider 인터페이스 구현
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initializeUserSystem()
        initializeLanguage() // 🆕 언어 초기화 추가
    }
    
    /**
     * 언어 초기화
     * - 저장된 사용자 언어 설정을 LanguageManager에 반영
     */
    private fun initializeLanguage() {
        val entryPoint = EntryPointAccessors.fromApplication(this, UseCaseEntryPoint::class.java)
        val getUserPreferenceUseCase = entryPoint.getUserPreferenceUseCase()

        CoroutineScope(Dispatchers.Main).launch { // 🆕 Main 디스패처로 변경
            getUserPreferenceUseCase().collect { userPref ->
                val langCode = userPref.languageCode.ifBlank { "en" }
                Logger.d("저장된 언어 확인: $langCode")
                // 🆕 LanguageManager 업데이트
                com.timor.kidsstory.domain.util.LanguageManager.setCurrentLanguageCode(langCode)
                updateLanguage(langCode)
            }
        }
    }

    /**
     * 사용자 시스템 초기화
     * - 현재는 default_user 생성/로드
     * - 향후 회원 시스템 도입 시 확장 가능
     */
    private fun initializeUserSystem() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userManager.initializeUser()
                Logger.d("사용자 시스템 초기화 완료")
            } catch (e: Exception) {
                Logger.e("사용자 시스템 초기화 실패: ${e.message}")
            }
        }
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
