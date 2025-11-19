package com.timor.kidsstory.di

import com.timor.kidsstory.domain.usecase.attendance.CheckTodayAttendanceUseCase
import com.timor.kidsstory.domain.usecase.attendance.GetAttendanceStatusUseCase
import com.timor.kidsstory.domain.usecase.book.FilterBooksUseCase
import com.timor.kidsstory.domain.usecase.book.GetAllBooksUseCase
import com.timor.kidsstory.domain.usecase.language.ChangeLanguageUseCase
import com.timor.kidsstory.domain.usecase.progress.GetReadingProgressUseCase
import com.timor.kidsstory.domain.usecase.progress.UpdateReadingProgressUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Clean Architecture Phase 1에서 생성된 UseCase들의 의존성 주입 모듈
 * 
 * @Inject 생성자를 사용하는 UseCase들은 자동으로 주입되지만,
 * 명시적 관리와 문서화를 위해 별도 모듈로 정의
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * Book 관련 UseCase들
     * 
     * GetAllBooksUseCase와 FilterBooksUseCase는 @Inject 생성자를 사용하므로
     * 자동으로 주입되지만, 명시적 문서화를 위해 주석으로 표시
     */
    
    // GetAllBooksUseCase - 자동 주입됨 (@Inject constructor)
    // FilterBooksUseCase - 자동 주입됨 (@Inject constructor)
    
    /**
     * Attendance 관련 UseCase들
     */
    
    // CheckTodayAttendanceUseCase - 자동 주입됨 (@Inject constructor)
    // GetAttendanceStatusUseCase - 자동 주입됨 (@Inject constructor)
    
    /**
     * Progress 관련 UseCase들
     */
    
    // GetReadingProgressUseCase - 자동 주입됨 (@Inject constructor)
    // UpdateReadingProgressUseCase - 자동 주입됨 (@Inject constructor)
    
    /**
     * Language 관련 UseCase들
     */
    
    // ChangeLanguageUseCase - 자동 주입됨 (@Inject constructor)
    
    /**
     * Level Test 관련 UseCase들
     */
    
    // GetLevelTestQuestionUseCase - 자동 주입됨 (@Inject constructor)
    // PerformLevelTestUseCase - 자동 주입됨 (@Inject constructor)
    
    /**
     * 추후 필요시 명시적 @Provides를 통한 UseCase 제공 예시
     * 
     * @Provides
     * @Singleton
     * fun provideGetAllBooksUseCase(
     *     bookRepository: BookRepository
     * ): GetAllBooksUseCase {
     *     return GetAllBooksUseCase(bookRepository)
     * }
     */
}