package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName


/**
 * Data transfer object representing a beacon.
 *
 * @property balId The unique identifier for the beacon. Can be null.
 * @property nom The name of the beacon. Can be null.
 * @property lieu The location of the beacon.
 * @property defaultMessage The default message ID associated with the beacon. Can be null.
 * @property volume The volume level of the beacon.
 * @property sysOnOff Indicates if the system is on or off.
 * @property autovolume Indicates if automatic volume control is enabled.
 * @property ipBal The IP address of the beacon.
 */
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


