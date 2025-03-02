package com.timor.kidsstory.domain.util

import android.content.Context
import android.media.MediaPlayer
import com.timor.kidsstory.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


/*
* 배경음 관리 매니저
* */
@Singleton
class MusicManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null


    init {
        // mediaplayer 설정
        mediaPlayer = MediaPlayer.create(context, R.raw.bgm_bookshelf).apply {
            isLooping = true        // 반복
            setVolume(1.0f, 1.0f) // 볼륨
        }
    }


    fun startMusic() {
        mediaPlayer?.let { player ->
            if(!player.isPlaying) player.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.let { player ->
            if(player.isPlaying) player.pause()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}