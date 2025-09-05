package com.timor.kidsstory.domain.usecase

import com.timor.kidsstory.domain.model.AppVersionInfo
import com.timor.kidsstory.domain.repository.AppVersionRepository
import javax.inject.Inject

/**
 * 앱 버전 체크 UseCase
 * - 현재 버전과 최신 버전을 비교하여 업데이트 필요 여부 판단
 */
class CheckAppVersionUseCase @Inject constructor(
    private val appVersionRepository: AppVersionRepository
) {
    suspend operator fun invoke(): Result<AppVersionInfo?> {
        return try {
            val latestVersionResult = appVersionRepository.getLatestVersionInfo()
            
            if (latestVersionResult.isFailure) {
                return Result.failure(latestVersionResult.exceptionOrNull()!!)
            }
            
            val latestVersion = latestVersionResult.getOrThrow()
            val currentVersionCode = appVersionRepository.getCurrentVersionCode()
            
            // 업데이트가 필요한지 확인
            if (latestVersion.latestVersionCode > currentVersionCode) {
                Result.success(latestVersion)
            } else {
                Result.success(null) // 업데이트 불필요
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}