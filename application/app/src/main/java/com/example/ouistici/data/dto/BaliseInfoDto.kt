package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName

data class BaliseInfoDto(
    @SerializedName("balise") val balise: BaliseDto,
    @SerializedName("annonces") val annonces: List<AnnonceDto>,
    @SerializedName("timeslots") val timeslots: List<TimeslotDto>
)