package com.timor.kidsstory.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.timor.kidsstory.data.remote.network.AppVersionNetworkService
import com.timor.kidsstory.domain.model.AppVersionInfo
import com.timor.kidsstory.domain.repository.AppVersionRepository
import com.timor.kidsstory.domain.util.LanguageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 앱 버전 저장소 구현체
 */
class AppVersionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkService: AppVersionNetworkService
) : AppVersionRepository {

    override suspend fun getLatestVersionInfo(): Result<AppVersionInfo> {
        return try {
            val metadataResult = networkService.getAppMetadata()
            
            if (metadataResult.isFailure) {
                return Result.failure(metadataResult.exceptionOrNull()!!)
            }
            
            val metadataDto = metadataResult.getOrThrow()
            
            // 현재 언어 코드 가져오기
            val currentLanguageCode = getCurrentLanguageCode()
            Log.d("AppVersionRepository", "Current language code: $currentLanguageCode")
            
            // 언어별 메시지 선택 (우선순위: 현재 언어 -> 한국어 -> 영어)
            val updateMessage = metadataDto.updateMessage[currentLanguageCode] 
                ?: metadataDto.updateMessage["ko"] 
                ?: metadataDto.updateMessage["en"]
                ?: "업데이트가 가능합니다."
            
            // 릴리즈 노트도 언어별로 가져오기
            val releaseNotes = metadataDto.releaseNotes?.get(currentLanguageCode)
                ?: metadataDto.releaseNotes?.get("ko")
            
            val versionInfo = AppVersionInfo(
                latestVersionCode = metadataDto.latestAppVersionCode,
                latestVersionName = metadataDto.latestAppVersionName,
                updateMessage = updateMessage,
                downloadUrl = metadataDto.updateUrl,
                isUpdateRequired = metadataDto.isUpdateRequired,
                releaseNotes = releaseNotes
            )
            
            Result.success(versionInfo)
        } catch (e: Exception) {
            Log.e("AppVersionRepository", "Error getting latest version info", e)
            Result.failure(e)
        }
    }

    override fun getCurrentVersionCode(): Int {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppVersionRepository", "Error getting current version code", e)
            4 // 현재 버전 코드를 기본값으로
        }
    }

    override fun getCurrentVersionName(): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "1.1.0"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppVersionRepository", "Error getting current version name", e)
            "1.1.0"
        }
    }

    private fun getCurrentLanguageCode(): String {
        return try {
            // LanguageManager를 사용하여 현재 설정된 언어 코드 가져오기
            val languageCode = LanguageManager.getCurrentLanguageCode()
            
            // 언어 코드를 GitHub 메타데이터의 키와 매칭
            when {
                languageCode.startsWith("ko") -> "ko"
                languageCode.startsWith("en") -> "en"
                languageCode.startsWith("tet") -> "tet"
                languageCode.startsWith("mn") -> "mn"
                else -> "ko" // 기본값
            }
        } catch (e: Exception) {
            Log.e("AppVersionRepository", "Error getting current language", e)
            "ko" // 기본값
        }
    }
}