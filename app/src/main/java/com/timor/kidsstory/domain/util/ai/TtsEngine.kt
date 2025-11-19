package com.timor.kidsstory.domain.util.ai

import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.FloatBuffer

class TtsEngine(
    fastSpeechModelPath: String,
    mbMelganModelPath: String,
    configPath: String
) {
    private val fastSpeech2: Interpreter
    private val mbMelgan: Interpreter
    private val charToId: Map<Char, Int>
    private val pad: String
    private val eos: String

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        fastSpeech2 = Interpreter(File(fastSpeechModelPath), options)
        mbMelgan = Interpreter(File(mbMelganModelPath), options)

        // Parse config.json to build the character map dynamically
        val configFile = File(configPath)
        val jsonString = configFile.readText()
        val jsonObject = JSONObject(jsonString)
        val charactersObject = jsonObject.getJSONObject("characters")
        
        pad = charactersObject.optString("pad", "_")
        eos = charactersObject.optString("eos", "~")
        val symbols = charactersObject.getString("characters")

        // Build charToId map from the characters in config
        val allSymbols = pad + eos + symbols
        charToId = allSymbols.toList().withIndex().associate { it.value to it.index }
        Log.d("TtsEngine", "Initialized with ${charToId.size} characters from config. TtsEngine created.")
    }

    private fun textToIds(text: String): IntArray {
        // Simple text cleaning. More complex cleaning can be added if needed.
        val cleanedText = text.toLowerCase().trim()
        if (cleanedText.isEmpty()) {
            return IntArray(0)
        }
        // Convert text to a sequence of IDs based on the dynamic map
        val sequence = cleanedText.mapNotNull { charToId[it] }
        return sequence.toIntArray()
    }

    fun synthesize(text: String, speed: Float = 1.0f): FloatArray? {
        return try {
            val inputIds = textToIds(text)
            if (inputIds.isEmpty()) {
                Log.w("TtsEngine", "textToIds result is empty for input: '$text'")
                return null
            }

            val melSpectrogram = getMelSpectrogram(inputIds, speed)
            getAudio(melSpectrogram)
        } catch (e: Exception) {
            Log.e("TtsEngine", "Synthesis failed", e)
            null
        }
    }

    private fun getMelSpectrogram(inputIds: IntArray, speed: Float): TensorBuffer {
        fastSpeech2.resizeInput(0, intArrayOf(1, inputIds.size))
        fastSpeech2.allocateTensors()

        val inputs = Array(1) { inputIds }
        val speaker = Array(1) { IntArray(1) }
        val speedRatios = floatArrayOf(speed)
        val f0Ratios = floatArrayOf(1.0f)
        val energyRatios = floatArrayOf(1.0f)

        val inputMap = mapOf(
            0 to inputs,
            1 to speaker,
            2 to intArrayOf(0), // pitch
            3 to speedRatios,
            4 to f0Ratios,
            5 to energyRatios
        )

        val outputBuffer = FloatBuffer.allocate(1 * 80 * 400) // A reasonable max size
        val outputMap = mapOf(0 to outputBuffer)

        fastSpeech2.runForMultipleInputsOutputs(inputMap.values.toTypedArray(), outputMap)

        val outputTensor = fastSpeech2.getOutputTensor(0)
        val shape = outputTensor.shape()
        val numFrames = shape[1]
        val melSize = shape[2]

        val outputArray = FloatArray(numFrames * melSize)
        outputBuffer.rewind()
        outputBuffer.get(outputArray, 0, numFrames * melSize)

        val spectrogram = TensorBuffer.createFixedSize(intArrayOf(1, numFrames, melSize), DataType.FLOAT32)
        spectrogram.loadArray(outputArray)
        return spectrogram
    }

    private fun getAudio(input: TensorBuffer): FloatArray {
        mbMelgan.resizeInput(0, input.shape)
        mbMelgan.allocateTensors()

        val outputBuffer = FloatBuffer.allocate(input.shape[1] * 256 * 4)
        mbMelgan.run(input.buffer, outputBuffer)

        val audioArray = FloatArray(outputBuffer.position())
        outputBuffer.rewind()
        outputBuffer.get(audioArray)
        return audioArray
    }

    fun close() {
        Log.d("TtsEngine", "TtsEngine closed.")
        fastSpeech2.close()
        mbMelgan.close()
    }
}