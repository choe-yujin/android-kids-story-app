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
    
    // 효과음 활성화 상태
    private var isEnabled = true
    
    // 효과음 전체 볼륨 (0.0 ~ 1.0)
    private var masterVolume = 0.5f

    // 효과음 볼륨 설정 추가 1.5배 증가
    companion object {
        private const val BUTTON_CLICK_BASE_VOLUME = 0.135f   // 13.5% (0.0675f * 2 = 0.135f)
        private const val PAGE_FLIP_BASE_VOLUME = 0.03375f    // 3.375% (0.0225f * 1.5 = 0.03375f)
    }

    /**
     * 효과음 활성화/비활성화 설정
     * 
     * @param enabled true: 효과음 켜기, false: 효과음 끄기
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        Log.d("SoundEffectManager", "Sound effects ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * 효과음 전체 볼륨 설정
     * 
     * @param volume 설정할 볼륨 (0.0 ~ 1.0)
     */
    fun setVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        Log.d("SoundEffectManager", "Master volume set to: $masterVolume")
    }
    
    /**
     * 현재 효과음 볼륨 반환
     * 
     * @return 현재 볼륨 (0.0 ~ 1.0)
     */
    fun getVolume(): Float = masterVolume
    
    /**
     * 효과음 활성화 여부 확인
     * 
     * @return 효과음이 활성화되어 있으면 true, 아니면 false
     */
    fun isEnabled(): Boolean = isEnabled
    
    /**
     * 특정 효과음 재생
     * 
     * @param soundType 재생할 효과음 타입
     */
    fun playSound(soundType: SoundType) {
        // 효과음이 비활성화되어 있으면 재생하지 않음
        if (!isEnabled) {
            Log.d("SoundEffectManager", "Sound effects disabled, skipping ${soundType.name}")
            return
        }
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
                
                // 효과음 타입별 볼륨 설정 (masterVolume 적용)
                val effectiveVolume = when (soundType) {
                    SoundType.BUTTON_CLICK -> {
                        val volume = BUTTON_CLICK_BASE_VOLUME * masterVolume
                        player.setVolume(volume, volume)
                        Log.d("SoundEffectManager", "Playing button click at volume: $volume (base: $BUTTON_CLICK_BASE_VOLUME, master: $masterVolume)")
                        volume
                    }
                    SoundType.PAGE_FLIP -> {
                        val volume = PAGE_FLIP_BASE_VOLUME * masterVolume
                        player.setVolume(volume, volume)
                        Log.d("SoundEffectManager", "Playing page flip at volume: $volume (base: $PAGE_FLIP_BASE_VOLUME, master: $masterVolume)")
                        volume
                    }
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
     * 효과음 볼륨을 동적으로 조정
     * @param buttonVolume 버튼 클릭음 볼륨 (0.0f ~ 1.0f)
     * @param pageVolume 페이지 넘김음 볼륨 (0.0f ~ 1.0f)
     */
    fun adjustVolume(buttonVolume: Float? = null, pageVolume: Float? = null) {
        soundPlayers[SoundType.BUTTON_CLICK]?.let { player ->
            buttonVolume?.let { volume ->
                player.setVolume(volume, volume)
            }
        }
        
        soundPlayers[SoundType.PAGE_FLIP]?.let { player ->
            pageVolume?.let { volume ->
                player.setVolume(volume, volume)
            }
        }
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
