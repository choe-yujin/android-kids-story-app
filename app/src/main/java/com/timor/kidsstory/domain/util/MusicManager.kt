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

    /**
     * 초기화 - MediaPlayer 설정
     */
    init {
        // mediaplayer 설정 - 배경음, 반복 및 볼륨 설정
        mediaPlayer = MediaPlayer.create(context, R.raw.bgm_bookshelf).apply {
            isLooping = true  // 반복 재생 활성화
            setVolume(1.0f, 1.0f)  // 볼륨 설정 (좌/우)
        }
    }

    /**
     * 배경 음악 재생 시작
     * - 이미 재생 중일 경우 아무 작업 없음
     */
    fun startMusic() {
        mediaPlayer?.let { player ->
            if(!player.isPlaying) player.start()
        }
    }

    /**
     * 배경 음악 정지
     * - 재생 중일 경우에만 정지
     */
    fun stopMusic() {
        mediaPlayer?.let { player ->
            if(player.isPlaying) player.pause()
        }
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