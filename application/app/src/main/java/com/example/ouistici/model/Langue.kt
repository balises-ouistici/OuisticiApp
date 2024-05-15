package com.example.ouistici.model

/**
 * @brief Represents a language.
 * @param code The language code.
 * @param nom The name of the language.
 */
class Langue(val code: String, val nom: String) {

    /**
     * @brief Returns the name of the language.
     * @return The name of the language.
     */
    fun getLangueName() : String {
        return nom
    }
}

