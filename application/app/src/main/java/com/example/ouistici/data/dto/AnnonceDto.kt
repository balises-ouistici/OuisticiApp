package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName

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
