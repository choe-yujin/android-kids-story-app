package com.timor.kidsstory.domain.usecase

import android.util.Log
import com.timor.kidsstory.domain.model.AppVersionInfo
import com.timor.kidsstory.domain.repository.AppVersionRepository
import com.timor.kidsstory.domain.repository.UpdateCheckRepository
import javax.inject.Inject

/**
 * 앱 버전 체크 UseCase
 * - 현재 버전과 최신 버전을 비교하여 업데이트 필요 여부 판단
 * - 네트워크 오류 처리 및 업데이트 빈도 제어
 */
class CheckAppVersionUseCase @Inject constructor(
    private val appVersionRepository: AppVersionRepository,
    private val updateCheckRepository: UpdateCheckRepository
) {
    suspend operator fun invoke(): Result<AppVersionInfo?> {
        return try {
            val currentVersionCode = appVersionRepository.getCurrentVersionCode()
            Log.d("CheckAppVersionUseCase", "Current version code: $currentVersionCode")
            
            // 1. 업데이트 체크가 필요한지 확인 (빈도 제어)
            if (!updateCheckRepository.shouldCheckForUpdate(currentVersionCode)) {
                Log.d("CheckAppVersionUseCase", "Update check skipped due to frequency control or user dismissal")
                return Result.success(null)
            }
            
            // 2. 마지막 체크 시간 업데이트
            updateCheckRepository.updateLastCheckTime(System.currentTimeMillis())
            
            // 3. 최신 버전 정보 가져오기 (네트워크 요청)
            Log.d("CheckAppVersionUseCase", "Fetching latest version info...")
            val latestVersionResult = appVersionRepository.getLatestVersionInfo()
            
            if (latestVersionResult.isFailure) {
                val error = latestVersionResult.exceptionOrNull()
                Log.w("CheckAppVersionUseCase", "Failed to check for updates (network error)", error)
                // 네트워크 오류는 사용자에게 방해가 되지 않도록 null 반환
                return Result.success(null)
            }
            
            val latestVersion = latestVersionResult.getOrThrow()
            Log.d("CheckAppVersionUseCase", "Latest version from server: ${latestVersion.latestVersionCode}")
            
            // 4. 업데이트가 필요한지 확인
            if (latestVersion.latestVersionCode > currentVersionCode) {
                Log.d("CheckAppVersionUseCase", 
                    "Update available: current=$currentVersionCode, latest=${latestVersion.latestVersionCode}")
                Result.success(latestVersion)
            } else {
                Log.d("CheckAppVersionUseCase", 
                    "App is up to date: current=$currentVersionCode, latest=${latestVersion.latestVersionCode}")
                Result.success(null) // 업데이트 불필요
            }
        } catch (e: Exception) {
            Log.e("CheckAppVersionUseCase", "Unexpected error during update check", e)
            // 예외 발생 시에도 앱 정상 동작을 위해 null 반환
            Result.success(null)
        }
    }
}