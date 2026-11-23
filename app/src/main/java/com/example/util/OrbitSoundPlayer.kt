package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object OrbitSoundPlayer {
    @Volatile
    var isEnabled: Boolean = true

    private var toneGenerator: ToneGenerator? = null
    private val lock = Any()

    private fun playTone(toneType: Int, durationMs: Int, volume: Int = 65) {
        if (!isEnabled) return
        synchronized(lock) {
            runCatching {
                if (toneGenerator == null) {
                    toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, volume)
                }
                toneGenerator?.startTone(toneType, durationMs)
            }.onFailure { e ->
                Log.w("OrbitSoundPlayer", "Audio unavailable: ${e.message}")
            }
        }
    }

    fun playClick() = playTone(ToneGenerator.TONE_PROP_BEEP, 24, 60)

    fun playClick(enabled: Boolean) {
        if (enabled) playTone(ToneGenerator.TONE_PROP_BEEP, 24, 60)
    }

    fun playTabSwoop() = playTone(ToneGenerator.TONE_PROP_BEEP2, 36, 55)

    fun playSuccess() = playTone(ToneGenerator.TONE_PROP_ACK, 55, 72)

    fun playSuccess(enabled: Boolean) {
        if (enabled) playTone(ToneGenerator.TONE_PROP_ACK, 55, 72)
    }

    fun playShieldBlock() = playTone(ToneGenerator.TONE_SUP_PIP, 32, 50)

    fun playGlyphBlip() = playTone(ToneGenerator.TONE_DTMF_9, 28, 65)

    fun playError() = playTone(ToneGenerator.TONE_PROP_NACK, 70, 70)
}
