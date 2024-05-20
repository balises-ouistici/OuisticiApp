package com.example.ouistici.data.dto

import com.example.ouistici.model.Langue
import com.example.ouistici.model.TypeAnnonce
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serial

class AnnonceDto(
    @SerializedName("balId") val  balId : Int?,
    @SerializedName("id") val  id : Int,
    @SerializedName("nom") val  nom : String,
    @SerializedName("type") val  type : String,
    // @SerializedName("audio") val  audio: ?,
    @SerializedName("contenu") val  contenu : String?,
    @SerializedName("langue") val  langue : String?,
    @SerializedName("duree") val  duree : Int?,
)
