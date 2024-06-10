package com.example.ouistici.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

/**
 * @brief Implements the AudioRecorder interface to record audio on Android.
 * @param context The context of the Android application.
 */
class AndroidAudioRecorder(
    private val context: Context
) : AudioRecorder {

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false

    private val sampleRate = 44100 // Sample rate in Hz
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO // Channel configuration
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT // PCM format
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    override fun start(outputFile: File) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ToastUtil.showToast(context, "Permission d'enregistrement non autorisée !")
            return
        }
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize)

        audioRecord?.startRecording()
        isRecording = true

        recordingThread = Thread {
            writeAudioDataToFile(outputFile)
        }
        recordingThread?.start()
    }

    private fun writeAudioDataToFile(outputFile: File) {
        val data = ByteArray(bufferSize)
        FileOutputStream(outputFile).use { fos ->
            while (isRecording) {
                val read = audioRecord?.read(data, 0, data.size) ?: 0
                if (read > 0) {
                    fos.write(data, 0, read)
                }
            }
        }
        writeWavHeader(outputFile)
    }

    private fun writeWavHeader(outputFile: File) {
        val fileLength = outputFile.length()
        RandomAccessFile(outputFile, "rw").use { raf ->
            raf.seek(0)
            // Write WAV header
            raf.writeBytes("RIFF") // ChunkID
            raf.writeInt((fileLength - 8).toInt().reverseBytes()) // ChunkSize
            raf.writeBytes("WAVE") // Format
            raf.writeBytes("fmt ") // Subchunk1ID
            raf.writeInt(16.reverseBytes()) // Subchunk1Size (PCM)
            raf.writeShort(1.toShort().reverseBytes().toInt()) // AudioFormat (PCM)
            raf.writeShort(1.toShort().reverseBytes().toInt()) // NumChannels (Mono)
            raf.writeInt(sampleRate.reverseBytes()) // SampleRate
            raf.writeInt((sampleRate * 2).reverseBytes()) // ByteRate
            raf.writeShort(2.toShort().reverseBytes().toInt()) // BlockAlign
            raf.writeShort(16.toShort().reverseBytes().toInt()) // BitsPerSample
            raf.writeBytes("data") // Subchunk2ID
            raf.writeInt((fileLength - 44).toInt().reverseBytes()) // Subchunk2Size
        }
    }

    override fun stop() {
        if (isRecording) {
            isRecording = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            recordingThread?.join()
            recordingThread = null
        }
    }
}

fun Int.reverseBytes(): Int {
    return ((this shl 24) or ((this and 0xFF00) shl 8) or ((this shr 8) and 0xFF00) or (this ushr 24))
}

fun Short.reverseBytes(): Short {
    return (((this.toInt() shl 8) and 0xFF00) or ((this.toInt() shr 8) and 0xFF)).toShort()
}


