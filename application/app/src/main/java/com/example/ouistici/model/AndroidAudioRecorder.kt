package com.example.ouistici.model

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

/**
 * @brief Implements the AudioRecorder interface to record audio on Android.
 * @param context The context of the Android application.
 */
class AndroidAudioRecorder(
    private val context: Context
) : AudioRecorder {

    private var recorder : MediaRecorder? = null

    /**
     * @brief Creates a new MediaRecorder audio recorder based on the Android version.
     * @return A new MediaRecorder audio recorder.
     */
    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    /**
     * @brief Starts audio recording and writes data to the specified output file.
     * @param outputFile The output file to record audio to.
     */
    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            // Enhance audio quality
            setAudioSamplingRate(44100) // Set sample rate
            setAudioEncodingBitRate(128000) // Set bit rate
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    /**
     * @brief Stops the ongoing audio recording.
     */
    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}