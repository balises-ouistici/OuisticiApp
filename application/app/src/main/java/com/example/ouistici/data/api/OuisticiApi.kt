package com.example.ouistici.data.api

import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.model.Balise
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface OuisticiApi {
    @Headers("Content-Type: application/json")
    @POST("volume")
    fun setVolume(@Body volume: Float): Call<BaliseDto>

    @Headers("Content-Type: application/json")
    @POST("infos")
    fun setNameAndPlace(@Body nameAndPlace: JsonObject): Call<BaliseDto>
}