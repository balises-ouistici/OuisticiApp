package com.example.ouistici.data.entity

import androidx.room.TypeConverter
import com.example.ouistici.model.PlageHoraire
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlageHoraireListConverter {
    @TypeConverter
    fun fromPlageHoraireList(value: List<PlageHoraire>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<PlageHoraire>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPlageHoraireList(value: String?): List<PlageHoraire>? {
        val gson = Gson()
        val type = object : TypeToken<List<PlageHoraire>>() {}.type
        return gson.fromJson(value, type)
    }
}