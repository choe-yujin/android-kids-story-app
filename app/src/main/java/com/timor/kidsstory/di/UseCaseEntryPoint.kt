package com.timor.kidsstory.di

import com.timor.kidsstory.domain.usecase.preference.GetUserPreferenceUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface UseCaseEntryPoint {
    fun getUserPreferenceUseCase(): GetUserPreferenceUseCase
}