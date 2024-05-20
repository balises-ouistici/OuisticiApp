package com.example.ouistici.data.dto

import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.PlageHoraire
import com.google.gson.annotations.SerializedName

data class BaliseDto(
    @SerializedName("balId") val  balId : Int?,
    @SerializedName("nom") val  nom : String?,
    @SerializedName("lieu") val  lieu : String,
    @SerializedName("defaultMessage") val  defaultMessage: Int?,
    @SerializedName("volume") val  volume : Float,
    @SerializedName("sysOnOff") val  sysOnOff : Boolean,
    @SerializedName("ipBal") val  ipBal : String,
)


