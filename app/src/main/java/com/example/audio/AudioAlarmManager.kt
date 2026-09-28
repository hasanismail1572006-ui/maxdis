package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Native synthetic audio & alarm manager.
 * Generates beeps directly using synthetic waveform oscillator (no external MP3 files needed),
 * equivalent to Web Audio API AudioContext oscillator.
 */
class AudioAlarmManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 90)
        } catch (e: Exception) {
            Log.e("AudioAlarmManager", "Failed to init ToneGenerator", e)
        }
    }

    /**
     * Plays synthetic beep alert when a timer reaches 0:00.
     */
    fun playTimerFinishedAlarm() {
        scope.launch {
            try {
                // Try ToneGenerator first
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
            } catch (e: Exception) {
                // Fallback to pure sine wave synthesis via AudioTrack
                playSyntheticTone(frequencyHz = 880.0, durationMs = 350)
            }
            vibrateAlert(pattern = longArrayOf(0, 200, 100, 200))
        }
    }

    /**
     * Plays error buzzer / warning alert when Save is clicked prematurely
     * (Indikasi Tidak Menggunakan Max Display).
     */
    fun playWarningAlarm() {
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 500)
            } catch (e: Exception) {
                playSyntheticTone(frequencyHz = 300.0, durationMs = 450)
            }
            vibrateAlert(pattern = longArrayOf(0, 100, 80, 100, 80, 250))
        }
    }

    /**
     * Plays a pleasant chime when Save is valid (Display Sesuai Waktu).
     */
    fun playSuccessChime() {
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 200)
            } catch (e: Exception) {
                playSyntheticTone(frequencyHz = 1046.5, durationMs = 200)
            }
            vibrateAlert(pattern = longArrayOf(0, 100))
        }
    }

    /**
     * Pure synthetic sine wave generator via AudioTrack (Web Audio API equivalent).
     */
    private fun playSyntheticTone(frequencyHz: Double, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / frequencyHz)
            val sample = (sin(angle) * 32767).toInt().toShort()
            generatedSnd[2 * i] = (sample.toInt() and 0x00ff).toByte()
            generatedSnd[2 * i + 1] = ((sample.toInt() and 0xff00) ushr 8).toByte()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
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
                .setBufferSizeInBytes(generatedSnd.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
        } catch (e: Exception) {
            Log.e("AudioAlarmManager", "Error playing synthetic tone", e)
        }
    }

    private fun vibrateAlert(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            // Non-critical if vibration fails
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // Ignore
        }
    }
}
