package com.example.chrono_fit_app.infrastructure.audio

import android.content.Context
import android.media.SoundPool
import com.example.chrono_fit_app.R
import com.example.chrono_fit_app.domain.audio.MusicPlayerI


class MusicPlayer(context: Context) : MusicPlayerI {
    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private val pitido: Int = soundPool.load(context, R.raw.beep, 1)
    private val pitidoFinal: Int = soundPool.load(context, R.raw.beep_end, 1)

    fun tocarPitido() {
        soundPool.play(pitido, 1f, 1f, 0, 0, 1f)
    }

    fun tocarPitidoFinal() {
        soundPool.play(pitidoFinal, 1f, 1f, 0, 0, 1f)
    }

    override fun playPitido() {
        tocarPitido()
    }
    override fun playPitidoFinal() {
        tocarPitidoFinal()
    }

    override fun release() {
        soundPool.release()
    }
}