package com.timor.kidsstory.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.timor.kidsstory.domain.model.UpdateCheckSettings
import com.timor.kidsstory.domain.repository.UpdateCheckRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 업데이트 체크 설정 리포지토리 구현체 (SharedPreferences 사용)
 */
@Singleton
class UpdateCheckRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UpdateCheckRepository {

    companion object {
        private const val PREFS_NAME = "update_check_settings"
        private const val KEY_LAST_CHECK_TIME = "last_check_time"
        private const val KEY_LAST_DISMISSED_VERSION = "last_dismissed_version"
        private const val KEY_DISMISSED_UNTIL = "dismissed_until"
        private const val KEY_CHECK_INTERVAL_HOURS = "check_interval_hours"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun getUpdateCheckSettings(): Flow<UpdateCheckSettings> = flow {
        emit(
            UpdateCheckSettings(
                lastUpdateCheckTime = prefs.getLong(KEY_LAST_CHECK_TIME, 0L),
                lastDismissedVersion = prefs.getInt(KEY_LAST_DISMISSED_VERSION, 0),
                dismissedUntil = prefs.getLong(KEY_DISMISSED_UNTIL, 0L),
                checkIntervalHours = prefs.getLong(KEY_CHECK_INTERVAL_HOURS, 6L) // 6시간으로 단축
            )
        )
    }

    override suspend fun updateLastCheckTime(time: Long) {
        Log.d("UpdateCheckRepository", "Updating last check time to: ${dateFormat.format(time)}")
        prefs.edit()
            .putLong(KEY_LAST_CHECK_TIME, time)
            .apply()
    }

    override suspend fun saveDismissedUpdate(versionCode: Int, dismissUntil: Long) {
        Log.d("UpdateCheckRepository", "Dismissing version $versionCode until: ${dateFormat.format(dismissUntil)}")
        prefs.edit()
            .putInt(KEY_LAST_DISMISSED_VERSION, versionCode)
            .putLong(KEY_DISMISSED_UNTIL, dismissUntil)
            .apply()
    }

    override suspend fun shouldCheckForUpdate(currentVersionCode: Int): Boolean {
        val settings = UpdateCheckSettings(
            lastUpdateCheckTime = prefs.getLong(KEY_LAST_CHECK_TIME, 0L),
            lastDismissedVersion = prefs.getInt(KEY_LAST_DISMISSED_VERSION, 0),
            dismissedUntil = prefs.getLong(KEY_DISMISSED_UNTIL, 0L),
            checkIntervalHours = prefs.getLong(KEY_CHECK_INTERVAL_HOURS, 6L) // 6시간으로 단축
        )
        
        val currentTime = System.currentTimeMillis()
        
        Log.d("UpdateCheckRepository", "=== UPDATE CHECK ANALYSIS ===")
        Log.d("UpdateCheckRepository", "Current time: ${dateFormat.format(currentTime)}")
        Log.d("UpdateCheckRepository", "Last check time: ${if (settings.lastUpdateCheckTime == 0L) "Never" else dateFormat.format(settings.lastUpdateCheckTime)}")
        Log.d("UpdateCheckRepository", "Last dismissed version: ${settings.lastDismissedVersion}")
        Log.d("UpdateCheckRepository", "Dismissed until: ${if (settings.dismissedUntil == 0L) "Never" else dateFormat.format(settings.dismissedUntil)}")
        Log.d("UpdateCheckRepository", "Check interval: ${settings.checkIntervalHours} hours")
        
        // 1. 첫 실행인지 확인 (lastUpdateCheckTime이 0이면 첫 실행)
        if (settings.lastUpdateCheckTime == 0L) {
            Log.d("UpdateCheckRepository", "✅ First run - allowing update check")
            return true
        }
        
        // 2. 체크 간격 확인
        val timeSinceLastCheck = currentTime - settings.lastUpdateCheckTime
        val checkIntervalMillis = settings.checkIntervalHours * 60 * 60 * 1000
        val hoursElapsed = timeSinceLastCheck / (60 * 60 * 1000.0)
        
        Log.d("UpdateCheckRepository", "Time since last check: ${String.format("%.1f", hoursElapsed)} hours")
        
        if (timeSinceLastCheck < checkIntervalMillis) {
            Log.d("UpdateCheckRepository", "❌ Too soon to check (need ${settings.checkIntervalHours} hours)")
            return false
        }
        
        // 3. "나중에" 버튼으로 무시된 버전인지 확인
        if (settings.dismissedUntil > currentTime) {
            val hoursRemaining = (settings.dismissedUntil - currentTime) / (60 * 60 * 1000.0)
            Log.d("UpdateCheckRepository", "❌ Version dismissed for ${String.format("%.1f", hoursRemaining)} more hours")
            return false
        }
        
        Log.d("UpdateCheckRepository", "✅ Update check allowed")
        Log.d("UpdateCheckRepository", "=== END ANALYSIS ===")
        return true
    }
}