package com.example.chrono_fit_app.infrastructure.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.chrono_fit_app.R
import com.example.chrono_fit_app.domain.audio.MusicPlayerI

class MusicPlayer(context: Context) : MusicPlayerI {

    private val soundPool: SoundPool

    private val pitido: Int
    private val pitidoFinal: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(1)
            .build()

        pitido = soundPool.load(context, R.raw.beep, 1)
        pitidoFinal = soundPool.load(context, R.raw.beep_end, 1)
    }

    override fun playPitido() {
        soundPool.play(pitido, 1f, 1f, 0, 0, 1f)
    }

    override fun playPitidoFinal() {
        soundPool.play(pitidoFinal, 1f, 1f, 0, 0, 1f)
    }

    override fun release() {
        soundPool.release()
    }
}
