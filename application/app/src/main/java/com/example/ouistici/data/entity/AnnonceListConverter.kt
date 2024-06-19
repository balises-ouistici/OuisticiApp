package com.example.ouistici.data.entity

import androidx.room.TypeConverter
import com.example.ouistici.model.Annonce
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Converter class for transforming a JSON string to a list of [Annonce] objects and vice versa.
 *
 * This class is used by Room to store complex data types in the database as strings.
 */
class AnnonceListConverter {
    /**
     * Converts a JSON string to a list of [Annonce] objects.
     *
     * @param value The JSON string representing a list of announcements.
     * @return A list of [Annonce] objects parsed from the JSON string.
     */
    @TypeConverter
    fun fromString(value: String): List<Annonce> {
        val listType = object : TypeToken<List<Annonce>>() {}.type
        return Gson().fromJson(value, listType)
    }

    /**
     * Converts a list of [Annonce] objects to a JSON string.
     *
     * @param list The list of announcements to be converted.
     * @return A JSON string representing the list of announcements.
     */
    @TypeConverter
    fun fromList(list: List<Annonce>): String {
        return Gson().toJson(list)
    }
}