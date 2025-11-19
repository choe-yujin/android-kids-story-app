package com.timor.kidsstory.domain.util

import android.content.Context
import android.media.MediaPlayer
import com.timor.kidsstory.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * 배경음 관리 클래스
 * - 배경 음악 재생, 정지, 리소스 관리 담당
 * - 싱글톤으로 앱 전체에서 하나의 인스턴스 공유
 *
 * @property context 애플리케이션 컨텍스트
 */
@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // MediaPlayer 인스턴스
    private var mediaPlayer: MediaPlayer? = null
    
    // 현재 음량 저장
    private var currentVolume: Float = 0.1f  // 기본 음량을 10%로 설정 (훨씬 낮게)

    /**
     * 초기화 - MediaPlayer 설정
     */
    init {
        // mediaplayer 설정 - 배경음, 반복 및 볼륨 설정
        mediaPlayer = MediaPlayer.create(context, R.raw.bgm_bookshelf).apply {
            isLooping = true  // 반복 재생 활성화
            setVolume(currentVolume * 0.1f, currentVolume * 0.1f)  // 훨씬 낮은 기본 볼륨으로 설정 (0.7 * 0.1 = 0.07)
        }
    }

    /**
     * 배경 음악 재생 시작
     * - 이미 재생 중일 경우 아무 작업 없음
     */
    fun startMusic() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    android.util.Log.d("MusicManager", "Music started")
                } else {
                    android.util.Log.d("MusicManager", "Music already playing")
                }
            } ?: android.util.Log.e("MusicManager", "MediaPlayer is null when starting music")
        } catch (e: Exception) {
            android.util.Log.e("MusicManager", "Error starting music", e)
        }
    }

    /**
     * 배경 음악 정지
     * - 재생 중일 경우에만 정지
     */
    fun stopMusic() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    android.util.Log.d("MusicManager", "Music stopped")
                } else {
                    android.util.Log.d("MusicManager", "Music already stopped")
                }
            } ?: android.util.Log.e("MusicManager", "MediaPlayer is null when stopping music")
        } catch (e: Exception) {
            android.util.Log.e("MusicManager", "Error stopping music", e)
        }
    }
    
    /**
     * 배경 음악 음량 설정
     * - 음량을 0.0 ~ 1.0 범위로 설정
     * 
     * @param volume 설정할 음량 (0.0 ~ 1.0)
     */
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        currentVolume = clampedVolume
        
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying || !player.isPlaying) {  // 재생 중이든 아니든 음량 설정
                    val actualVolume = currentVolume * 0.1f  // 훨씬 낮은 볼륨으로 조정
                    player.setVolume(actualVolume, actualVolume)
                    android.util.Log.d("MusicManager", "Volume set to: $currentVolume (actual: $actualVolume)")
                }
            } ?: android.util.Log.e("MusicManager", "MediaPlayer is null when setting volume")
        } catch (e: Exception) {
            android.util.Log.e("MusicManager", "Error setting volume", e)
        }
    }

    /**
     * 현재 음량 반환
     * 
     * @return 현재 설정된 음량 (0.0 ~ 1.0)
     */
    fun getCurrentVolume(): Float {
        return currentVolume
    }
    
    /**
     * 리소스 해제
     * - 앱 종료 또는 뷰모델 소멸 시 호출
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
