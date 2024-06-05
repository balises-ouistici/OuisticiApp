package com.example.ouistici.data.entity

import androidx.room.TypeConverter
import com.example.ouistici.model.Annonce
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AnnonceListConverter {
    @TypeConverter
    fun fromString(value: String): List<Annonce> {
        val listType = object : TypeToken<List<Annonce>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Annonce>): String {
        return Gson().toJson(list)
    }
}