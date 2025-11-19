package com.timor.kidsstory.domain.util.ai

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TtsManager(
    private val modelDownloader: ModelDownloadManager
) {
    private var ttsEngine: TtsEngine? = null
    private var audioTrack: AudioTrack? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    private val _state = MutableStateFlow<TtsState>(TtsState.Idle)
    val state = _state.asStateFlow()

    init {
        initAudioTrack()
        checkAndInitialize()
    }

    private fun initAudioTrack() {
        try {
            val sampleRate = 22050
            val channelConfig = AudioFormat.CHANNEL_OUT_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_FLOAT
            val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .build()
            // Do NOT call play() here. Call it right before writing.
        } catch (e: Exception) {
            Logger.e(e, "Failed to initialize AudioTrack")
            _state.value = TtsState.Error("AudioTrack initialization failed.")
        }
    }

    private fun checkAndInitialize() {
        coroutineScope.launch {
            if (modelDownloader.isModelDownloaded("tts_en_sample")) {
                initializeTtsEngine()
            } else {
                _state.value = TtsState.NotDownloaded
            }
        }
    }

    fun downloadAndInitialize() {
        coroutineScope.launch {
            if (_state.value is TtsState.Downloading) return@launch

            _state.value = TtsState.Downloading(0f)

            modelDownloader.downloadModels("tts_en_sample").collect { progress ->
                when (progress) {
                    is DownloadProgress.InProgress -> {
                        _state.value = TtsState.Downloading(progress.progress)
                    }
                    is DownloadProgress.Completed -> {
                        initializeTtsEngine()
                    }
                    is DownloadProgress.Error -> {
                        _state.value = TtsState.Error(progress.message)
                    }
                }
            }
        }
    }

    private suspend fun initializeTtsEngine() {
        withContext(Dispatchers.IO) {
            try {
                Logger.d("TtsManager: Initializing TtsEngine...") // Added log
                _state.value = TtsState.Initializing
                // Assume getModelPaths now returns a Triple with the config path
                val modelPaths = modelDownloader.getModelPaths("tts_en_sample")
                if (modelPaths != null) {
                    ttsEngine?.close()
                    ttsEngine = TtsEngine(
                        fastSpeechModelPath = modelPaths.first,  // fastspeech2.tflite
                        mbMelganModelPath = modelPaths.second, // mbmelgan.tflite
                        configPath = modelPaths.third      // config.json
                    )
                    _state.value = TtsState.Ready
                    Logger.d("TtsManager: TTS Engine initialized successfully with dynamic config.")
                } else {
                    _state.value = TtsState.Error("Failed to get model paths.")
                    Logger.e("TtsManager: Could not get model paths after download.")
                }
            } catch (e: Exception) {
                _state.value = TtsState.Error("TTS engine initialization failed: ${e.message}")
                Logger.e(e, "TtsManager: TTS Engine initialization failed.")
            }
        }
    }

    fun speak(text: String) {
        if (_state.value != TtsState.Ready) {
            Logger.w("TtsManager: Not ready to speak. Current state: ${_state.value}")
            if (_state.value == TtsState.NotDownloaded || _state.value is TtsState.Error) { // Added condition for Error state
                downloadAndInitialize()
            }
            return
        }

        coroutineScope.launch {
            try {
                _state.value = TtsState.Synthesizing
                val audioData = ttsEngine?.synthesize(text)
                if (audioData != null && audioData.isNotEmpty()) {
                    playAudio(audioData)
                } else {
                    _state.value = TtsState.Error("Synthesis returned null or empty audio data.")
                }
            } catch (e: Exception) {
                _state.value = TtsState.Error("Synthesis failed: ${e.message}")
                Logger.e(e, "TtsManager: Synthesis failed.")
            }
        }
    }

    private suspend fun playAudio(audioData: FloatArray) {
        withContext(Dispatchers.IO) {
            _state.value = TtsState.Playing
            try {
                audioTrack?.play() // Play right before writing data
                audioTrack?.write(audioData, 0, audioData.size, AudioTrack.WRITE_BLOCKING)
                audioTrack?.stop() // Stop after writing to flush buffer and prepare for next playback
            } catch (e: Exception) {
                _state.value = TtsState.Error("Audio playback failed: ${e.message}")
                Logger.e(e, "TtsManager: Audio playback failed.")
            } finally {
                if (_state.value !is TtsState.Error) {
                    _state.value = TtsState.Ready
                }
            }
        }
    }

    fun release() {
        Logger.d("TtsManager: Releasing resources.") // Added log
        ttsEngine?.close()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}

sealed class TtsState {
    object Idle : TtsState()
    object NotDownloaded : TtsState()
    data class Downloading(val progress: Float) : TtsState()
    object Initializing : TtsState()
    object Ready : TtsState()
    object Synthesizing : TtsState()
    object Playing : TtsState()
    data class Error(val message: String) : TtsState()
}