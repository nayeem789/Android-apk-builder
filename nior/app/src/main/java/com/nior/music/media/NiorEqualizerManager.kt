package com.nior.music.media

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.util.Log

class NiorEqualizerManager(private var audioSessionId: Int) {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    var isEnabled: Boolean = true
        private set

    init {
        initEffects()
    }

    fun updateAudioSession(newSessionId: Int) {
        if (audioSessionId != newSessionId) {
            release()
            audioSessionId = newSessionId
            initEffects()
        }
    }

    private fun initEffects() {
        try {
            if (audioSessionId != 0) {
                equalizer = Equalizer(0, audioSessionId).apply {
                    enabled = isEnabled
                }
                bassBoost = BassBoost(0, audioSessionId).apply {
                    enabled = isEnabled
                }
            }
        } catch (e: Exception) {
            Log.e("NiorEqualizer", "Failed to initialize hardware audio effects", e)
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.isEnabled = enabled
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
        } catch (e: Exception) {
            Log.e("NiorEqualizer", "Error setting effect enabled state", e)
        }
    }

    fun getBandCount(): Short = equalizer?.numberOfBands ?: 5

    fun getBandCenterFreq(band: Short): Int = equalizer?.getCenterFreq(band) ?: 1000

    fun getBandLevelRange(): ShortArray = equalizer?.bandLevelRange ?: shortArrayOf(-1500, 1500)

    fun getBandLevel(band: Short): Short = equalizer?.getBandLevel(band) ?: 0

    fun setBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (e: Exception) {
            Log.e("NiorEqualizer", "Failed to set band level", e)
        }
    }

    fun setBassBoostStrength(strength: Short) { // 0 to 1000
        try {
            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(strength)
            }
        } catch (e: Exception) {
            Log.e("NiorEqualizer", "Failed to set bass boost", e)
        }
    }

    fun usePreset(presetIndex: Short) {
        try {
            equalizer?.usePreset(presetIndex)
        } catch (e: Exception) {
            Log.e("NiorEqualizer", "Failed to use preset", e)
        }
    }

    fun release() {
        try {
            equalizer?.release()
            equalizer = null
            bassBoost?.release()
            bassBoost = null
        } catch (e: Exception) {
            Log.e("NiorEqualizer", "Error releasing effects", e)
        }
    }
}
