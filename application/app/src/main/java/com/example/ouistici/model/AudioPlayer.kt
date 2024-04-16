package com.example.ouistici.model

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}