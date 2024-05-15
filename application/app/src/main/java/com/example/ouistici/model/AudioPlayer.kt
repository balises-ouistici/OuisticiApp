package com.example.ouistici.model

import java.io.File

/**
 * @brief Defines the behavior of an audio player.
 */
interface AudioPlayer {

    /**
     * @brief Plays the specified audio file.
     * @param file The audio file to play.
     */
    fun playFile(file: File)

    /**
     * @brief Stops the audio playback.
     */
    fun stop()
}