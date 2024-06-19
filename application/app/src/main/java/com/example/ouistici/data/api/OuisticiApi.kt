package com.example.ouistici.data.api

import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.BaliseInfoDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.dto.TimeslotDto
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Interface for interacting with the Ouistici API.
 */
interface OuisticiApi {
    // Get toute la balise

    /**
     * Retrieves information about the beacon.
     *
     * @return [Call] with the beacon information encapsulated in [BaliseInfoDto].
     */
    @GET("balise")
    fun getBaliseInfo(): Call<BaliseInfoDto>
    

    // Page infos balise

    /**
     * Sets the volume for the beacon.
     *
     * @param volume JSON object containing the volume information.
     * @return [Call] with the updated beacon information encapsulated in [BaliseDto].
     */
    @Headers("Content-Type: application/json")
    @POST("volume")
    fun setVolume(@Body volume: JsonObject): Call<BaliseDto>


    /**
     * Sets the name and location of the beacon.
     *
     * @param nameAndPlace JSON object containing the name and location.
     * @return [Call] with the updated beacon information encapsulated in [BaliseDto].
     */
    @Headers("Content-Type: application/json")
    @POST("infos")
    fun setNameAndPlace(@Body nameAndPlace: JsonObject): Call<BaliseDto>


    /**
     * Tests the beacon sound.
     *
     * @return [Call] without a response (void).
     */
    @GET("test_sound")
    fun testSound(): Call<Void>


    /**
     * Toggles automatic volume control.
     *
     * @param buttonState JSON object containing the button state (enabled or disabled).
     * @return [Call] with the updated beacon information encapsulated in [BaliseDto].
     */
    @Headers("Content-Type: application/json")
    @POST("autovolume")
    fun setAutoVolume(@Body buttonState: JsonObject): Call<BaliseDto>


    /**
     * Downloads the audio for a specific announcement.
     *
     * @param id_annonce ID of the announcement for which the audio is downloaded.
     * @return [Call] with the response body [ResponseBody] containing the audio file.
     */
    @GET("annonce/sound/{id_annonce}")
    fun downloadAudio(@Path("id_annonce") id_annonce: Int): Call<ResponseBody>


    // Page choix annonce

    /**
     * Sets the default message.
     *
     * @param defaultMessage JSON object containing the default message.
     * @return [Call] with the updated beacon information encapsulated in [BaliseDto].
     */
    @Headers("Content-Type: application/json")
    @POST("defaultmessage")
    fun setDefaultMessage(@Body defaultMessage: JsonObject): Call<BaliseDto>


    /**
     * Sets the button state for time slots.
     *
     * @param buttonState JSON object containing the button state.
     * @return [Call] with the updated beacon information encapsulated in [BaliseDto].
     */
    @Headers("Content-Type: application/json")
    @POST("plages_horaire")
    fun setButtonState(@Body buttonState: JsonObject): Call<BaliseDto>


    // Création d'annonce

    /**
     * Creates a new announcement.
     *
     * @param annonce JSON object containing the details of the announcement.
     * @return [Call] with the created announcement information encapsulated in [AnnonceDto].
     */
    @Headers("Content-Type: application/json")
    @PUT("annonce")
    fun createAnnonce(@Body annonce: JsonObject): Call<AnnonceDto>


    /**
     * Modifies an existing announcement.
     *
     * @param annonce JSON object containing the details of the modified announcement.
     * @return [Call] with the modified announcement information encapsulated in [AnnonceDto].
     */
    @Headers("Content-Type: application/json")
    @POST("annonce")
    fun modifyAnnonce(@Body annonce: JsonObject): Call<AnnonceDto>


    /**
     * Deletes an announcement.
     *
     * @param annonce JSON object containing the details of the announcement to delete.
     * @return [Call] with the deleted announcement information encapsulated in [AnnonceDto].
     */
    @Headers("Content-Type: application/json")
    @POST("delannonce")
    fun deleteAnnonce(@Body annonce: JsonObject): Call<AnnonceDto>


    /**
     * Creates an audio for an announcement.
     *
     * @param description Description of the audio.
     * @param audiofile Audio file to upload.
     * @return [Call] with the announcement file information encapsulated in [FileAnnonceDto].
     */
    @Multipart
    @POST("annonce/upload_sound")
    fun createAudio(
        @Part("description") description: RequestBody,
        @Part audiofile: MultipartBody.Part
    ): Call<FileAnnonceDto>


    // Plages horaires

    /**
     * Creates a time slot.
     *
     * @param timeslot JSON object containing the details of the time slot.
     * @return [Call] with the created time slot information encapsulated in [TimeslotDto].
     */
    @Headers("Content-Type: application/json")
    @PUT("timeslots")
    fun createTimeslot(@Body timeslot: JsonObject): Call<TimeslotDto>


    /**
     * Modifies an existing time slot.
     *
     * @param timeslot JSON object containing the details of the modified time slot.
     * @return [Call] with the modified time slot information encapsulated in [TimeslotDto].
     */
    @Headers("Content-Type: application/json")
    @POST("timeslots")
    fun modifyTimeslot(@Body timeslot: JsonObject): Call<TimeslotDto>


    /**
     * Deletes a time slot.
     *
     * @param timeslot JSON object containing the details of the time slot to delete.
     * @return [Call] with the deleted time slot information encapsulated in [TimeslotDto].
     */
    @Headers("Content-Type: application/json")
    @POST("deltimeslot")
    fun deleteTimeslot(@Body timeslot: JsonObject): Call<TimeslotDto>
}