package com.timor.kidsstory.di

import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import com.timor.kidsstory.domain.usecase.CheckAppVersionUseCase
import com.timor.kidsstory.domain.usecase.PostponeUpdateUseCase
import com.timor.kidsstory.domain.repository.UpdateCheckRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface UseCaseEntryPoint {
    fun getUserPreferenceUseCase(): GetUserPreferenceUseCase
    fun getCheckAppVersionUseCase(): CheckAppVersionUseCase
    fun getPostponeUpdateUseCase(): PostponeUpdateUseCase
    fun getUpdateCheckRepository(): UpdateCheckRepository
}