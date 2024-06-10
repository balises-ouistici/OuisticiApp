package com.example.ouistici.data.api

import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.BaliseInfoDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.dto.TimeslotDto
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface OuisticiApi {
    // Get toute la balise
    @GET("balise")
    fun getBaliseInfo(): Call<BaliseInfoDto>
    

    // Page infos balise
    @Headers("Content-Type: application/json")
    @POST("volume")
    fun setVolume(@Body volume: JsonObject): Call<BaliseDto>

    @Headers("Content-Type: application/json")
    @POST("infos")
    fun setNameAndPlace(@Body nameAndPlace: JsonObject): Call<BaliseDto>

    @GET("test_sound")
    fun testSound(): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("autovolume")
    fun setAutoVolume(@Body buttonState: JsonObject): Call<BaliseDto>


    // Page choix annonce
    @Headers("Content-Type: application/json")
    @POST("defaultmessage")
    fun setDefaultMessage(@Body defaultMessage: JsonObject): Call<BaliseDto>

    @Headers("Content-Type: application/json")
    @POST("plages_horaire")
    fun setButtonState(@Body buttonState: JsonObject): Call<BaliseDto>


    // Création d'annonce
    @Headers("Content-Type: application/json")
    @PUT("annonce")
    fun createAnnonce(@Body annonce: JsonObject): Call<AnnonceDto>

    @Headers("Content-Type: application/json")
    @POST("annonce")
    fun modifyAnnonce(@Body annonce: JsonObject): Call<AnnonceDto>

    @Headers("Content-Type: application/json")
    @POST("delannonce")
    fun deleteAnnonce(@Body annonce: JsonObject): Call<AnnonceDto>

    @Multipart
    @POST("annonce/upload_sound")
    fun createAudio(
        @Part("description") description: RequestBody,
        @Part audiofile: MultipartBody.Part
    ): Call<FileAnnonceDto>


    // Plages horaires
    @Headers("Content-Type: application/json")
    @PUT("timeslots")
    fun createTimeslot(@Body timeslot: JsonObject): Call<TimeslotDto>

    @Headers("Content-Type: application/json")
    @POST("timeslots")
    fun modifyTimeslot(@Body timeslot: JsonObject): Call<TimeslotDto>

    @Headers("Content-Type: application/json")
    @POST("deltimeslot")
    fun deleteTimeslot(@Body timeslot: JsonObject): Call<TimeslotDto>


}