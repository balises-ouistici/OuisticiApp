package com.example.ouistici.model

import java.io.File


interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}