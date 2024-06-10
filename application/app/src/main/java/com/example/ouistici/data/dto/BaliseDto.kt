package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName

data class BaliseDto(
    @SerializedName("idBal") val  balId : Int?,
    @SerializedName("nom") val  nom : String?,
    @SerializedName("lieu") val  lieu : String,
    @SerializedName("default_message") val  defaultMessage: Int?,
    @SerializedName("volume") val  volume : Float,
    @SerializedName("timeslots") val  sysOnOff : Boolean,
    @SerializedName("autovolume") val  autovolume : Boolean,
    @SerializedName("ipBal") val  ipBal : String,
)


