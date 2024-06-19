package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName


/**
 * Data transfer object representing a time slot for an announcement.
 *
 * @property id_timeslot The unique identifier for the time slot. Can be null.
 * @property id_annonce The unique identifier for the associated announcement.
 * @property monday Indicates if the time slot is active on Monday.
 * @property tuesday Indicates if the time slot is active on Tuesday.
 * @property wednesday Indicates if the time slot is active on Wednesday.
 * @property thursday Indicates if the time slot is active on Thursday.
 * @property friday Indicates if the time slot is active on Friday.
 * @property saturday Indicates if the time slot is active on Saturday.
 * @property sunday Indicates if the time slot is active on Sunday.
 * @property time_start The start time of the time slot in HH:mm format.
 * @property time_end The end time of the time slot in HH:mm format.
 */
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