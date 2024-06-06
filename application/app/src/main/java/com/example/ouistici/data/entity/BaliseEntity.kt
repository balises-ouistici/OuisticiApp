package com.example.ouistici.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.PlageHoraire

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