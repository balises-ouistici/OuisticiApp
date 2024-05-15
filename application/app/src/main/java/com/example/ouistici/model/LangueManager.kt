package com.example.ouistici.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * @brief Manages the available languages and the current language.
 */
class LangueManager {
    companion object {
        /**
         * @brief The list of available languages.
         */
        val languesDisponibles = listOf(
            Langue("fr", "Français"),
            Langue("en", "English")
        )

        /**
         * @brief The current language.
         */
        var langueActuelle: Langue by mutableStateOf(languesDisponibles.first())
    }
}