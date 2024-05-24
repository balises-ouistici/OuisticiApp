package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName

class TimeslotDto(
    @SerializedName("id_timeslot") val  id_timeslot : Int?,
    @SerializedName("id_annonce") val  id_annonce : Int,
    @SerializedName("monday") val  monday : Boolean,
    @SerializedName("tuesday") val  tuesday: Boolean,
    @SerializedName("wednesday") val  wednesday : Boolean,
    @SerializedName("thursday") val  thursday : Boolean,
    @SerializedName("friday") val  friday : Boolean,
    @SerializedName("saturday") val  saturday : Boolean,
    @SerializedName("sunday") val  sunday : Boolean,
    @SerializedName("time_start") val  time_start : String,
    @SerializedName("time_end") val  time_end : String
)