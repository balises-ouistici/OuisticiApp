package com.example.ouistici.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.PlageHoraire

/**
 * Entity class representing a beacon in the database.
 *
 * @property id Primary key auto-generated by Room.
 * @property nom Name of the beacon.
 * @property lieu Location of the beacon.
 * @property defaultMessage Default message associated with the beacon. Can be null.
 * @property annonces List of announcements associated with the beacon.
 * @property volume Volume level of the beacon.
 * @property plages List of time slots (plage horaire) associated with the beacon.
 * @property sysOnOff Boolean indicating if the beacon system is on or off.
 * @property ipBal IP address of the beacon.
 */
@Entity(tableName = "balises")
data class BaliseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nom: String,
    val lieu: String,
    val defaultMessage: String?,
    val annonces: List<Annonce>, // Assuming Annonce is a Parcelable or a supported type
    val volume: Float,
    val plages: List<PlageHoraire>, // Assuming PlageHoraire is a Parcelable or a supported type
    val sysOnOff: Boolean,
    var ipBal: String
)