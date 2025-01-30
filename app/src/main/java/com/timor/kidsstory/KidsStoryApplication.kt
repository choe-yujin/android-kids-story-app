package com.timor.kidsstory

import android.app.Application
import com.timor.kidsstory.data.repository.StoryRepositoryImpl
import com.timor.kidsstory.domain.repository.StoryRepository
import com.timor.kidsstory.domain.usecase.GetStoryUseCase
import com.timor.kidsstory.domain.usecase.PageNavigationUseCase
import com.timor.kidsstory.domain.util.FileUtils

// 앱의 전역 상태와 의존성을 관리하는 Application 클래스
// Repository와 UseCase들의 의존성 주입을 담당
class KidsStoryApplication : Application() {
    // Repository
    lateinit var storyRepository: StoryRepository
        private set

    // UseCases
    lateinit var getStoryUseCase: GetStoryUseCase
        private set
    lateinit var pageNavigationUseCase: PageNavigationUseCase
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize dependencies
        val fileUtils = FileUtils(this)
        storyRepository = StoryRepositoryImpl(this, fileUtils)

        // Initialize use cases
        getStoryUseCase = GetStoryUseCase(storyRepository)  // Repository만 전달
        pageNavigationUseCase = PageNavigationUseCase()
    }
}