package com.timor.kidsstory.domain.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.timor.kidsstory.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 효과음 관리 클래스
 * - 버튼 클릭, 페이지 넘김 등의 효과음 재생
 * - 싱글톤으로 앱 전체에서 하나의 인스턴스 공유
 *
 * @property context 애플리케이션 컨텍스트
 */
@Singleton
class SoundEffectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 효과음 타입 정의
    enum class SoundType(val resourceId: Int) {
        BUTTON_CLICK(R.raw.button_click),
        PAGE_FLIP(R.raw.page_flip)
    }

    // MediaPlayer 인스턴스들을 저장하는 맵
    private val soundPlayers = mutableMapOf<SoundType, MediaPlayer>()

    /**
     * 특정 효과음 재생
     * 
     * @param soundType 재생할 효과음 타입
     */
    fun playSound(soundType: SoundType) {
        try {
            // 기존 플레이어가 있으면 정리
            soundPlayers[soundType]?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }

            // 새로운 플레이어 생성 및 재생
            val mediaPlayer = MediaPlayer.create(context, soundType.resourceId)
            mediaPlayer?.let { player ->
                soundPlayers[soundType] = player
                
                // 재생 완료 리스너 설정 (자동 정리)
                player.setOnCompletionListener { mp ->
                    mp.release()
                    soundPlayers.remove(soundType)
                }
                
                // 효과음 타입별 볼륨 설정
                when (soundType) {
                    SoundType.BUTTON_CLICK -> player.setVolume(0.3f, 0.3f)  // 버튼 클릭음
                    SoundType.PAGE_FLIP -> player.setVolume(0.1f, 0.1f)     // 페이지 넘김음 (더 작게)
                }
                
                player.start()
            }
        } catch (e: Exception) {
            Log.e("SoundEffectManager", "Error playing sound: ${soundType.name}", e)
        }
    }

    /**
     * 버튼 클릭 효과음 재생
     */
    fun playButtonClick() {
        playSound(SoundType.BUTTON_CLICK)
    }

    /**
     * 페이지 넘김 효과음 재생
     */
    fun playPageFlip() {
        playSound(SoundType.PAGE_FLIP)
    }

    /**
     * 모든 효과음 정지 및 리소스 해제
     */
    fun release() {
        soundPlayers.values.forEach { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                Log.e("SoundEffectManager", "Error releasing sound player", e)
            }
        }
        soundPlayers.clear()
    }

    /**
     * 특정 효과음 정지
     * 
     * @param soundType 정지할 효과음 타입
     */
    fun stopSound(soundType: SoundType) {
        soundPlayers[soundType]?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
                soundPlayers.remove(soundType)
            } catch (e: Exception) {
                Log.e("SoundEffectManager", "Error stopping sound: ${soundType.name}", e)
            }
        }
    }
}
