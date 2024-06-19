package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object representing an announcement.
 *
 * @property upload_sound_url The URL for uploading the announcement sound. Can be null.
 * @property id_annonce The unique identifier for the announcement.
 * @property nom The name of the announcement.
 * @property type The type of the announcement.
 * @property contenu The content of the announcement. Can be null.
 * @property langue The language of the announcement. Can be null.
 * @property duree The duration of the announcement in seconds. Can be null.
 * @property filename The name of the file associated with the announcement. Can be null.
 */
class AnnonceDto(
    @SerializedName("upload_sound_url") val  upload_sound_url : String?,
    @SerializedName("id_annonce") val  id_annonce : Int,
    @SerializedName("nom") val  nom : String,
    @SerializedName("type") val  type : String,
    @SerializedName("contenu") val  contenu : String?,
    @SerializedName("langue") val  langue : String?,
    @SerializedName("duree") val  duree : Int?,
    @SerializedName("filename") val  filename : String?
)
