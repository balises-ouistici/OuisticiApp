package com.example.ouistici.data.entity

import androidx.room.TypeConverter
import com.example.ouistici.model.PlageHoraire
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Converter class for transforming a list of `PlageHoraire` objects to and from a JSON string.
 *
 * This class is used by Room to store and retrieve lists of `PlageHoraire` objects in the database.
 */
class PlageHoraireListConverter {
    /**
     * Converts a list of `PlageHoraire` objects to a JSON string.
     *
     * @param value The list of `PlageHoraire` objects to convert.
     * @return A JSON string representing the list of `PlageHoraire` objects.
     */
    @TypeConverter
    fun fromPlageHoraireList(value: List<PlageHoraire>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<PlageHoraire>>() {}.type
        return gson.toJson(value, type)
    }

    /**
     * Converts a JSON string to a list of `PlageHoraire` objects.
     *
     * @param value The JSON string representing a list of `PlageHoraire` objects.
     * @return A list of `PlageHoraire` objects parsed from the JSON string.
     */
    @TypeConverter
    fun toPlageHoraireList(value: String?): List<PlageHoraire>? {
        val gson = Gson()
        val type = object : TypeToken<List<PlageHoraire>>() {}.type
        return gson.fromJson(value, type)
    }
}