package com.example.ouistici.model

import java.io.File

/**
 * @brief Defines the behavior of an audio recorder.
 */
interface AudioRecorder {

    /**
     * @brief Starts recording audio and writes it to the specified output file.
     * @param outputFile The output file to record audio to.
     */
    fun start(outputFile: File)

    /**
     * @brief Stops the audio recording.
     */
    fun stop()
}