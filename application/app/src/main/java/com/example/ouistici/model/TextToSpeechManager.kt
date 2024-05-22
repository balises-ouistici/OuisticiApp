package com.example.ouistici.model

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
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

    fun saveToFile(text: String, file: File, name: String) {
        if (isInitialized) {
            val utteranceId = "AuthorizationManager.createCodeVerifier()"
            val params: HashMap<String, String> = HashMap()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
            tts.synthesizeToFile(text, params, file.path);
        }
    }

    fun shutdown() {
        if (isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
