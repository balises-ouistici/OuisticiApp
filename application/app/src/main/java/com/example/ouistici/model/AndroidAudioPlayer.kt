package com.example.ouistici.model

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

/**
 * Implementation of AudioPlayer interface using Android's MediaPlayer.
 *
 * @property context The context used for creating MediaPlayer instances.
 */
class AndroidAudioPlayer(
    private val context: Context
) : AudioPlayer {

    private var player : MediaPlayer? = null

    /**
     * Plays the audio file using MediaPlayer.
     *
     * @param file The File object representing the audio file to be played.
     */
    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    /**
     * Stops the currently playing audio and releases resources.
     */
    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    companion object {
        /**
         * Retrieves the duration of an audio file.
         *
         * @param file The File object representing the audio file.
         * @return The duration of the audio file in milliseconds, or 0 if an error occurs.
         */
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