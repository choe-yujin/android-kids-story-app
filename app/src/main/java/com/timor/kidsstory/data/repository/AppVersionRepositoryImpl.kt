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
import java.util.Locale
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
            
            // 현재 언어 코드 가져오기 (개선된 로직)
            val currentLanguageCode = getCurrentLanguageCode()
            Log.d("AppVersionRepository", "=== LANGUAGE DETECTION DEBUG ===")
            Log.d("AppVersionRepository", "Current language code: $currentLanguageCode")
            Log.d("AppVersionRepository", "Available message languages: ${metadataDto.updateMessage.keys}")
            
            // 언어별 메시지 선택 (우선순위: 현재 언어 -> 한국어 -> 영어)
            val updateMessage = when {
                metadataDto.updateMessage.containsKey(currentLanguageCode) -> {
                    Log.d("AppVersionRepository", "✅ Found exact match for '$currentLanguageCode'")
                    metadataDto.updateMessage[currentLanguageCode]!!
                }
                metadataDto.updateMessage.containsKey("ko") -> {
                    Log.d("AppVersionRepository", "⚠️ Using Korean fallback (requested: $currentLanguageCode)")
                    metadataDto.updateMessage["ko"]!!
                }
                metadataDto.updateMessage.containsKey("en") -> {
                    Log.d("AppVersionRepository", "⚠️ Using English fallback (requested: $currentLanguageCode)")
                    metadataDto.updateMessage["en"]!!
                }
                else -> {
                    Log.w("AppVersionRepository", "❌ No suitable message found, using default")
                    "업데이트가 가능합니다."
                }
            }
            
            Log.d("AppVersionRepository", "Selected message: $updateMessage")
            
            // 릴리즈 노트도 언어별로 가져오기
            val releaseNotes = metadataDto.releaseNotes?.get(currentLanguageCode)
                ?: metadataDto.releaseNotes?.get("ko")
            
            Log.d("AppVersionRepository", "Selected release notes: $releaseNotes")
            Log.d("AppVersionRepository", "=== END DEBUG ===")
            
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
            packageInfo.versionName ?: "2.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppVersionRepository", "Error getting current version name", e)
            "2.0.0"
        }
    }

    private fun getCurrentLanguageCode(): String {
        return try {
            // 1. 실제 앱 로케일 확인 (가장 정확함)
            val appLocaleCode = context.resources.configuration.locale.language
            Log.d("AppVersionRepository", "🔍 App locale: '$appLocaleCode'")
            
            // 2. 시스템 로케일에서 가져오기
            val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
            val systemLanguageCode = systemLocale.language
            Log.d("AppVersionRepository", "🔍 System locale: '$systemLanguageCode'")
            
            // 3. LanguageManager에서 가져오기 (참고용)
            val languageManagerCode = LanguageManager.getCurrentLanguageCode()
            Log.d("AppVersionRepository", "🔍 LanguageManager code: '$languageManagerCode'")
            
            // 4. 우선순위: LanguageManager > 앱 로케일 > 시스템 로케일
            val candidateCode = when {
                // LanguageManager 사용 (최우선)
                languageManagerCode.isNotEmpty() -> {
                    Log.d("AppVersionRepository", "✅ Using LanguageManager (highest priority): $languageManagerCode")
                    languageManagerCode
                }
                // 앱 로케일이 한국어면 한국어 우선
                appLocaleCode == "ko" -> {
                    Log.d("AppVersionRepository", "✅ Using app locale (Korean): $appLocaleCode")
                    appLocaleCode
                }
                // 시스템 로케일이 한국어면 한국어 우선
                systemLanguageCode == "ko" -> {
                    Log.d("AppVersionRepository", "✅ Using system locale (Korean): $systemLanguageCode")
                    systemLanguageCode
                }
                // 앱 로케일 사용
                appLocaleCode.isNotEmpty() -> {
                    Log.d("AppVersionRepository", "✅ Using app locale: $appLocaleCode")
                    appLocaleCode
                }
                // 시스템 로케일 사용
                systemLanguageCode.isNotEmpty() -> {
                    Log.d("AppVersionRepository", "✅ Using system locale: $systemLanguageCode")
                    systemLanguageCode
                }
                else -> {
                    Log.w("AppVersionRepository", "⚠️ No valid locale found, using default Korean")
                    "ko"
                }
            }
            
            // 5. 언어 코드를 GitHub 메타데이터 키로 매핑
            val mappedCode = when {
                candidateCode == "ko" || candidateCode.startsWith("ko") -> "ko"
                candidateCode == "en" || candidateCode.startsWith("en") -> "en" 
                candidateCode == "tet" || candidateCode.contains("tet") -> "tet"
                candidateCode == "mn" || candidateCode.startsWith("mn") -> "mn"
                else -> {
                    Log.w("AppVersionRepository", "⚠️ Unknown language code: '$candidateCode', mapping to 'ko'")
                    "ko"
                }
            }
            
            Log.d("AppVersionRepository", "🎯 Final mapped code: '$mappedCode' (from '$candidateCode')")
            mappedCode
            
        } catch (e: Exception) {
            Log.e("AppVersionRepository", "❌ Error getting current language", e)
            "ko" // 기본값
        }
    }
}