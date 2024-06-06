package com.example.ouistici.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.util.Log
import java.io.File
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    fun setLanguage(locale: Locale) {
        if (isInitialized) {
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!")
            }
        }
    }

    fun saveToFile(text: String, file: File) {
        if (isInitialized) {
            val utteranceId = "AuthorizationManager.createCodeVerifier()"
            val params: HashMap<String, String> = HashMap()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
            tts.synthesizeToFile(text, params, file.path)
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            val utteranceId = "AuthorizationManager.createCodeVerifier()"
            val params: HashMap<String, String> = HashMap()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
            tts.speak(text, QUEUE_FLUSH, params)
        }
    }

    fun stop() {
        if (isInitialized) {
            tts.stop()
        }
    }


    fun shutdown() {
        if (isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}

