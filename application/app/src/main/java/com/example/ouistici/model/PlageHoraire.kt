package com.example.ouistici.model

import java.time.LocalTime

/**
 * @brief Represents a time slot during which a specific announcement is scheduled to be played.
 * @param nomMessage The announcement scheduled for this time slot.
 * @param jours The days of the week when the time slot is active.
 * @param heureDebut The start time of the time slot.
 * @param heureFin The end time of the time slot.
 */
class PlageHoraire(
    var id_timeslot: Int,
    var nomMessage : Annonce,
    var jours : List<JoursSemaine>,
    var heureDebut : LocalTime,
    var heureFin : LocalTime
)

