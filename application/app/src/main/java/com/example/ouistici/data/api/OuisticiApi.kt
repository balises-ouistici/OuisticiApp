package com.example.ouistici.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OuisticiApi {
    @POST("volume")
    fun setVolume(@Body volume: Float): Call<Void>

    @POST("infos")
    fun setVolume(@Body nom: String, lieu : String?): Call<Void>
}