package com.example.ouistici.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.util.Log
import java.io.File
import java.util.Locale

/**
 * Manages the Text-To-Speech functionality in an Android application.
 *
 * @property context The context used to initialize TextToSpeech.
 */
class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isInitialized = false

    /**
     * Called to indicate the completion of TextToSpeech initialization.
     *
     * @param status The status of the initialization. Either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    /**
     * Sets the language for TextToSpeech.
     *
     * @param locale The locale representing the language and country to set.
     */
    fun setLanguage(locale: Locale) {
        if (isInitialized) {
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!")
            }
        }
    }

    /**
     * Synthesizes the given text to a file using TextToSpeech.
     *
     * @param text The text to synthesize.
     * @param file The file where the synthesized speech will be saved.
     */
    fun saveToFile(text: String, file: File) {
        if (isInitialized) {
            val utteranceId = "AuthorizationManager.createCodeVerifier()"
            val params: HashMap<String, String> = HashMap()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
            tts.synthesizeToFile(text, params, file.path)
        }
    }

    /**
     * Speaks the given text using TextToSpeech.
     *
     * @param text The text to speak out loud.
     */
    fun speak(text: String) {
        if (isInitialized) {
            val utteranceId = "AuthorizationManager.createCodeVerifier()"
            val params: HashMap<String, String> = HashMap()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
            tts.speak(text, QUEUE_FLUSH, params)
        }
    }

    /**
     * Stops the speech synthesis if it's in progress.
     */
    fun stop() {
        if (isInitialized) {
            tts.stop()
        }
    }
}

