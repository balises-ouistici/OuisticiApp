package com.example.ouistici.model


/**
 * Represents a beacon or tag entity.
 *
 * @property id Unique identifier of the beacon.
 * @property nom Name of the beacon.
 * @property lieu Location of the beacon.
 * @property defaultMessage Default announcement associated with the beacon.
 * @property annonces List of announcements associated with the beacon.
 * @property volume Volume level of the beacon.
 * @property plages List of time slots associated with the beacon.
 * @property sysOnOff Indicates if the system associated with the beacon is on or off.
 * @property autovolume Indicates if automatic volume adjustment is enabled for the beacon.
 * @property ipBal IP address of the beacon.
 */
class Balise(
    val id: Int,
    var nom : String,
    var lieu : String,
    var defaultMessage : Annonce?,
    var annonces : ArrayList<Annonce>,
    var volume : Float,
    var plages : ArrayList<PlageHoraire>,
    var sysOnOff : Boolean,
    var autovolume : Boolean,
    var ipBal : String
) {

    /**
     * Generates a unique ID for a new announcement based on existing announcements.
     *
     * @return The next available ID for an announcement.
     */
    fun createId(): Int {
        if ( annonces.isEmpty() ) {
            return 0
        }
       return annonces.maxOf { it.id } + 1
    }

    /**
     * Generates a unique ID for a new time slot based on existing time slots.
     *
     * @return The next available ID for a time slot.
     */
    fun createIdTimeslot(): Int {
        if ( plages.isEmpty() ) {
            return 0
        }
        return plages.maxOf { it.id_timeslot } + 1
    }
}