package com.example.ouistici.model

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

/**
 * @brief Implements the AudioPlayer interface to play audio files on Android.
 * @param context The context of the Android application.
 */
class AndroidAudioPlayer(
    private val context: Context
) : AudioPlayer {

    private var player : MediaPlayer? = null

    /**
     * @brief Plays the specified audio file.
     * @param file The audio file to play.
     */
    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    /**
     * @brief Stops the audio playback.
     */
    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    companion object {
        fun getAudioDuration(file: File): Int {
            val mediaPlayer = MediaPlayer()
            return try {
                mediaPlayer.setDataSource(file.path)
                mediaPlayer.prepare()
                val duration = mediaPlayer.duration
                mediaPlayer.release()
                duration
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }
}