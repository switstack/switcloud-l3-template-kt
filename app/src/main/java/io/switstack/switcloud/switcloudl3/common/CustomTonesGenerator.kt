package io.switstack.switcloud.switcloudl3.common

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioTrack.PERFORMANCE_MODE_LOW_LATENCY
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.math.PI
import kotlin.math.sin

class CustomTonesGenerator {
    private val sampleRate = 44100
    private val successDurationMs = 500
    private val alertDurationMs = 600
    private var successAudioTrack: AudioTrack? = null
    private var alertAudioTrack: AudioTrack? = null

    init {

        val successSamples = generateSuccessAudioData()

        val alertSamples = generateAlertAudioData()

        successAudioTrack = generateAudioTrack(successSamples)
        alertAudioTrack = generateAudioTrack(alertSamples)
    }

    private fun generateSuccessAudioData(): ShortArray {
        val successFrequency = 1500.0
        val samplesLength = (successDurationMs * sampleRate) / 1000
        val audioData = ShortArray(samplesLength)

        generateSine(audioData, 0, samplesLength, successFrequency)

        return audioData
    }

    private fun generateAlertAudioData(): ShortArray {
        val alertFrequency = 750.0
        val samplesLength = (alertDurationMs * sampleRate) / 1000
        val audioData = ShortArray(samplesLength)

        val samplesPerSegment = samplesLength / 3

        generateSine(audioData, 0, samplesPerSegment, alertFrequency)
        // no sound for 200 ms in the middle
        generateSine(audioData, samplesPerSegment * 2, samplesPerSegment, alertFrequency)

        return audioData
    }

    private fun generateAudioTrack(audioDataSamples: ShortArray) =
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(audioDataSamples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setPerformanceMode(PERFORMANCE_MODE_LOW_LATENCY)
            .build().apply {
                write(audioDataSamples, 0, audioDataSamples.size)
            }

    private fun generateSine(data: ShortArray, start: Int, length: Int, freq: Double) {
        for (i in 0 until length) {
            val sample = sin(2.0 * PI * i / (sampleRate / freq))
            data[start + i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
    }

    fun playSuccess() = play(successAudioTrack, successDurationMs.toLong())

    fun playAlert() = play(alertAudioTrack, alertDurationMs.toLong())

    private fun play(audioTrack: AudioTrack?, durationInMs: Long) {
        audioTrack?.let {
            it.play()
            Timer().schedule(durationInMs) {
                it.stop()
            }
        }
    }
}