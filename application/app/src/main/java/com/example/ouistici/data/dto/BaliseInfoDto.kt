package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object representing information about a beacon, including its details,
 * associated announcements, and time slots.
 *
 * @property balise The details of the beacon, encapsulated in [BaliseDto].
 * @property annonces The list of announcements associated with the beacon, encapsulated in [AnnonceDto].
 * @property timeslots The list of time slots associated with the beacon, encapsulated in [TimeslotDto].
 */
data class BaliseInfoDto(
    @SerializedName("balise") val balise: BaliseDto,
    @SerializedName("annonces") val annonces: List<AnnonceDto>,
    @SerializedName("timeslots") val timeslots: List<TimeslotDto>
)