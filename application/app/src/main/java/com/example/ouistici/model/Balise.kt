package com.example.ouistici.model

/**
 * @brief Represents a beacon.
 * @param nom The name of the beacon.
 * @param lieu The location of the beacon (optional).
 * @param defaultMessage The default announcement for the beacon (optional).
 * @param annonces The list of announcements associated with the beacon.
 * @param volume The volume of the beacon.
 * @param plages The time slots during which the beacon operates.
 * @param sysOnOff The system status of the beacon (on/off).
 */
class Balise(
    var nom : String,
    var lieu : String,
    var defaultMessage : Annonce?,
    var annonces : ArrayList<Annonce>,
    var volume : Float,
    var plages : ArrayList<PlageHoraire>,
    var sysOnOff : Boolean,
    var ipBal : String
) {
    /**
     * @brief Generates a unique identifier for the beacon based on the maximum ID of its announcements.
     * @return The generated unique identifier.
     */
    fun createId(): Int {
        if ( annonces.isEmpty() ) {
            return 0
        }
       return annonces.maxOf { it.id } + 1
    }
}