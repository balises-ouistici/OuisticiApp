package com.example.ouistici.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LangueManager {
    companion object {
        val languesDisponibles = listOf(
            Langue("fr", "Français"),
            Langue("en", "Anglais")
        )

        var langueActuelle: Langue by mutableStateOf(languesDisponibles.first())
    }
}